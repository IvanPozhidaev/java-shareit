package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.validationmarkers.Create;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookingPostResponseDto {
    private Long id;
    private Item item;

    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;

    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime end;

    private BookingStatus status;

    private UserDto booker;
}