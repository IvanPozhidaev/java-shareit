package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking toModel(BookingPostDto dto, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingPostResponseDto toPostResponseDto(Booking booking, Item item) {
        BookingPostResponseDto dto = new BookingPostResponseDto();
        dto.setId(booking.getId());
        dto.setItem(item);
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setBooker(new UserDto(booking.getBooker().getId(), null, null));
        return dto;
    }

    public static BookingResponseDto toResponseDto(Booking booking, UserDto booker, Item item) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStatus(booking.getStatus());
        dto.setBooker(booker);
        dto.setItem(ItemMapper.toDto(item, null));
        dto.setName(item.getName());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        return dto;
    }

    public static BookingDetailedDto toDetailedDto(Booking booking) {
        BookingDetailedDto dto = new BookingDetailedDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setBooker(UserMapper.toDto(booking.getBooker()));
        dto.setItem(ItemMapper.toDto(booking.getItem()));
        dto.setName(booking.getItem().getName());
        return dto;
    }

    public static BookingInItemDto bookingInItemDto(Booking booking) {
        if (booking == null) return null;

        BookingInItemDto dto = new BookingInItemDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        return dto;
    }

    public static List<BookingDetailedDto> toListDetailedDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toDetailedDto).collect(Collectors.toList());
    }
}