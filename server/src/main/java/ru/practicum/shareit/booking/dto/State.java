package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.UnsupportedStatusException;

import static ru.practicum.shareit.booking.BookingServiceImpl.ILLEGAL_STATE_MESSAGE;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

   public static State parseState(String state) {
        State status;
        try {
            status = State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(ILLEGAL_STATE_MESSAGE + state);
        }
        return status;
    }
}