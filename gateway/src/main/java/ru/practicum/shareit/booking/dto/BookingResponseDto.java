package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookingResponseDto {
    private Long id;
    private BookingStatus status;
    private UserDto booker;
    private ItemDto item;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
}