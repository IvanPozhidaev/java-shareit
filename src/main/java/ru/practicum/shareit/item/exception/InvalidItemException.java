package ru.practicum.shareit.item.exception;

public class InvalidItemException extends RuntimeException {
    public InvalidItemException(String message) {
        super(message);
    }
}