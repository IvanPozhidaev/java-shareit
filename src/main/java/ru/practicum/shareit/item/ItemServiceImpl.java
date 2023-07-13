package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

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
            throw new OwnerNotFoundException("Не найден владелец c id: " + item.getOwner());
        }
        item = itemRepository.save(item);
        return ItemMapper.toDto(item, null);
    }

    @Override
    @Transactional
    public DetailedCommentDto createComment(CreateCommentDto dto, Long itemId, Long userId) {
        if (dto.getText().isBlank()) throw new CommentException("Комментарий не может быть пустым");
        Item item = itemRepository.findById(itemId).orElseThrow();
        User author = userRepository.findById(userId).orElseThrow();

        if (bookingRepository.findBookingsForAddComments(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new CommentException("Нельзя оставить комментарий на вещь, " +
                    "который вы не пользовались или ещё не закончился срок аренды" + " itemId: " + itemId);
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
            return getItemsWithBookingsAndComments(List.of(item), List.of(item.getId())).get(0);
        }
        return ItemMapper.toDto(item, null, null, comments == null ? List.of() : comments);
    }

    @Override
    public List<ItemDto> findAllItems(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Item> itemPage = itemRepository.findAll(userId, pageable);
        List<Item> userItems = itemPage.getContent();

        List<Long> itemIds = itemRepository.findAllByOwner(userId)
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        return getItemsWithBookingsAndComments(userItems, itemIds);

    }

    @Override
    public List<ItemDto> findItemsByRequest(String text, Long userId, int from, int size) {
        if (text == null || text.isBlank() || text.length() <= 3) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> foundItems = itemRepository.search(text, pageable).toList();
        List<Long> itemIds = foundItems.stream().map(Item::getId).collect(Collectors.toList());

        return getItemsWithBookingsAndComments(foundItems, itemIds);
    }

    private List<ItemDto> getItemsWithBookingsAndComments(List<Item> foundItems, List<Long> itemIds) {
        Map<Long, List<Comment>> comments = commentRepository.findAllByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        List<Booking> all = bookingRepository.findAll();
        System.out.println(LocalDateTime.now());
        Map<Long, List<Booking>> lastByItemIds = bookingRepository.findLastByItemIds(itemIds,
                        LocalDateTime.now(),
                        BookingStatus.APPROVED.name())
                .stream()
                .sorted((o1, o2) -> o2.getEnd().compareTo(o1.getEnd()))
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        Map<Long, List<Booking>> nextByItemIds = bookingRepository.findNextByItemIds(itemIds,
                        LocalDateTime.now(),
                        BookingStatus.APPROVED.name())
                .stream()
                .sorted((o1, o2) -> o1.getStart().compareTo(o2.getStart()))
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        return foundItems.stream()
                .map(item -> {
                            List<Booking> lastBookings = lastByItemIds.get(item.getId());
                            List<Booking> nextBookings = nextByItemIds.get(item.getId());
                            List<Comment> commentsItem = comments.get(item.getId());

                            return ItemMapper.toDto(item,
                                    lastBookings == null ? null : lastBookings.get(0),
                                    nextBookings == null ? null : nextBookings.get(0),
                                    commentsItem == null ? List.of() : commentsItem);
                        }
                )
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    private boolean isOwnerExists(long ownerId) {
        return userRepository.findById(ownerId).isPresent();
    }

    private Item refreshItem(Item patch) {
        Item entry = itemRepository.findById(patch.getId()).orElseThrow();

        if (!entry.getOwner().equals(patch.getOwner())) {
            throw new DeniedAccessException("Пользователь не является владельцем вещи" +
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
}