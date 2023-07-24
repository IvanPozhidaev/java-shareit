package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class BookingDetailedDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
    private UserDto booker;
    private ItemDto item;
    private String name;
}