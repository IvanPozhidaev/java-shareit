package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.DeniedAccessException;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.DetailedCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    public static final int MIN_SEARCH_REQUEST_LENGTH = 3;
    public static final String OWNER_NOT_FOUND_MESSAGE = "Не найден владелец c id: ";
    public static final String EMPTY_COMMENT_MESSAGE = "Комментарий не может быть пустым";
    public static final String DENIED_ACCESS_MESSAGE = "Пользователь не является владельцем вещи";
    public static final String COMMENT_EXCEPTION_MESSAGE = "Нельзя оставить комментарий на вещь, " +
            "который вы не пользовались или ещё не закончился срок аренды";

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toModel(itemDto, userId);
        boolean ownerExists = isOwnerExists(item.getOwner());
        if (!ownerExists) {
            throw new OwnerNotFoundException(OWNER_NOT_FOUND_MESSAGE + item.getOwner());
        }
        item = itemRepository.save(item);
        return ItemMapper.toDto(item, null);
    }

    @Override
    @Transactional
    public DetailedCommentDto createComment(CreateCommentDto dto, Long itemId, Long userId) {
        if (dto.getText().isBlank()) throw new CommentException(EMPTY_COMMENT_MESSAGE);
        Item item = itemRepository.findById(itemId).orElseThrow();
        User author = userRepository.findById(userId).orElseThrow();

        if (bookingRepository.findBookingsForAddComments(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new CommentException(COMMENT_EXCEPTION_MESSAGE + " itemId: " + itemId);
        }
        Comment comment = CommentMapper.toModel(dto, item, author);
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDetailedDto(comment);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = ItemMapper.toModel(itemDto, userId);
        item.setId(itemId);
        List<Comment> comments = commentRepository.findByItemId(itemId);
        item = itemRepository.save(refreshItem(item));
        return ItemMapper.toDto(item, comments);
    }

    @Override
    public ItemDto findItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        List<Comment> comments = commentRepository.findByItemId(itemId);

        if (item.getOwner().equals(userId)) {
            return constructItemDtoForOwner(item, comments);
        }
        return ItemMapper.toDto(item, null, null, comments);
    }

    @Override
    public List<ItemDto> findAllItems(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Item> itemPage = itemRepository.findAll(userId, pageable);
        List<Item> userItems = itemPage.getContent();

        List<ItemDto> result = new ArrayList<>();
        fillItemDtoList(result, userItems, userId);

        Comparator<ItemDto> itemDtoComparator = Comparator.comparing((ItemDto o) -> {
            if (o.getNextBooking() == null) {
                return LocalDateTime.MAX;
            }
            return o.getNextBooking().getStart();
        });
        result.sort(itemDtoComparator);

        return result;
    }

    @Override
    public List<ItemDto> findItemsByRequest(String text, Long userId, int from, int size) {
        if (text == null || text.isBlank() || text.length() <= MIN_SEARCH_REQUEST_LENGTH) {
            return Collections.emptyList();
        }
        List<ItemDto> result = new ArrayList<>();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> foundItems = itemRepository.search(text, pageable).toList();
        fillItemDtoList(result, foundItems, userId);
        return result;
    }

    private boolean isOwnerExists(long ownerId) {
        return userRepository.findById(ownerId).isPresent();
    }

    private Item refreshItem(Item patch) {
        Item entry = itemRepository.findById(patch.getId()).orElseThrow();

        if (!entry.getOwner().equals(patch.getOwner())) {
            throw new DeniedAccessException(DENIED_ACCESS_MESSAGE +
                    "userId: " + patch.getOwner() + ", itemId: " + patch.getId());
        }

        String name = patch.getName();
        if (name != null && !name.isBlank()) {
            entry.setName(name);
        }

        String description = patch.getDescription();
        if (description != null && !description.isBlank()) {
            entry.setDescription(description);
        }

        Boolean available = patch.getAvailable();
        if (available != null) {
            entry.setAvailable(available);
        }
        return entry;
    }

    private void fillItemDtoList(List<ItemDto> targetList, List<Item> foundItems, Long userId) {

        List<Item> userItems = itemRepository.findAllByOwner(userId);
        List<Long> itemIds = userItems.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findAllByItemIdIn(itemIds);

        itemIds.addAll(foundItems.stream()
                .filter(item -> !item.getOwner().equals(userId))
                .map(Item::getId)
                .collect(Collectors.toList()));

        List<ItemDto> ownerItems = foundItems.stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(item -> constructItemDtoForOwner(item, filterCommentsByItemId(comments, item.getId())))
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());

        List<ItemDto> otherItems = foundItems.stream()
                .filter(item -> !item.getOwner().equals(userId))
                .map(item -> constructItemDtoForOwner(item, filterCommentsByItemId(comments, item.getId())))
                .collect(Collectors.toList());

        targetList.addAll(ownerItems);
        targetList.addAll(otherItems);
    }

    private List<Comment> filterCommentsByItemId(List<Comment> comments, Long itemId) {
        return comments.stream().filter(c -> c.getItem().getId().equals(itemId)).collect(Collectors.toList());
    }

    private ItemDto constructItemDtoForOwner(Item item, List<Comment> comments) {
        List<Booking> lastBooking = bookingRepository.findLastBookingsByItemIdAndEndIsBeforeAndStatusIs(
                item.getId(), LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "end"));

        List<Booking> nextBooking = bookingRepository.findNextBookingsByItemIdAndEndIsAfterAndStatusIs(
                item.getId(), LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

        ItemDto itemDto = ItemMapper.toDto(item,
                lastBooking.isEmpty() ? null : lastBooking.get(0),
                nextBooking.isEmpty() ? null : nextBooking.get(0),
                comments);

        if (itemDto.getLastBooking() == null && itemDto.getNextBooking() != null) {
            itemDto.setLastBooking(itemDto.getNextBooking());
            itemDto.setNextBooking(null);
        }

        return itemDto;
    }
}