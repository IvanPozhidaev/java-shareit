package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.errorhandler.UnsupportedStatusException;

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
            throw new UnsupportedStatusException("  state: " + state);
        }
        return status;
    }
}