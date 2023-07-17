package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInRequestDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toDto(Item item, List<Comment> comments) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        if (comments != null) {
            dto.setComments(CommentMapper.toCommentDetailedDtoList(comments));
        }
        dto.setRequestId(item.getRequest() == null ? null : item.getRequest().getId());
        return dto;
    }

    public static ItemDto toDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }

    public static ItemDto toDto(Item item,
                                Booking lastBooking,
                                Booking nextBooking,
                                List<Comment> comments) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(BookingMapper.bookingInItemDto(lastBooking));
        dto.setNextBooking(BookingMapper.bookingInItemDto(nextBooking));
        if (comments != null) {
            dto.setComments(CommentMapper.toCommentDetailedDtoList(comments));
        }
        return dto;
    }

    public static ItemDto toDto(Item item, List<Booking> bookings, List<Comment> comments) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        List<BookingInItemDto> bookingInItemDtos = bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(item.getId()))
                .sorted(Comparator.comparing(Booking::getStart))
                .map(BookingMapper::bookingInItemDto)
                .collect(Collectors.toList());

        if (!bookingInItemDtos.isEmpty()) {
            dto.setLastBooking(bookingInItemDtos.get(0));
            dto.setNextBooking(bookingInItemDtos.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .findFirst().orElse(null));
        }

        if (comments != null) {
            dto.setComments(CommentMapper.toCommentDetailedDtoList(comments));
        }

        return dto;
    }

    public static Item toModel(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setRequest(itemDto.getRequestId() == null ? null : Request.builder().id(itemDto.getRequestId()).build());
        return item;
    }

    public static ItemInRequestDto toRequestItemDto(Item item) {
        ItemInRequestDto dto = new ItemInRequestDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequest().getId());
        dto.setOwner(item.getOwner().getId());
        return dto;
    }

    public static List<ItemInRequestDto> toRequestItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toRequestItemDto)
                .collect(Collectors.toList());
    }
}