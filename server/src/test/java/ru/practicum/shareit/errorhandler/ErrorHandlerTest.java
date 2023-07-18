package ru.practicum.shareit.errorhandler;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.*;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    public void handleOwnerNotFoundExceptionTest() {
        OwnerNotFoundException e = new OwnerNotFoundException("Не найден владелец вещи ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), e.getMessage());
    }

    @Test
    public void handleDeniedAccessExceptionTest() {
        DeniedAccessException e = new DeniedAccessException("Отказано в доступе ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), e.getMessage());
    }

    @Test
    public void handleNoSuchElementExceptionTest() {
        NoSuchElementException e = new NoSuchElementException("Ошибка поиска элемента 404: ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), "Ошибка поиска элемента 404: ");
        assertEquals(errorResponse.getDescription(), "Ошибка поиска элемента 404: ");
    }

    @Test
    public void handleUnavailableBookingExceptionTest() {
        UnavailableBookingException e = new UnavailableBookingException("Ошибка бронирования 400: ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "Ошибка бронирования 400: ");
    }

    @Test
    public void handleIllegalArgumentExceptionTest() {
        IllegalArgumentException e = new IllegalArgumentException("Передано недопустимое значение 400: ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "Передано недопустимое значение 400: ");
    }

    @Test
    public void handleUnsupportedStatusExceptionTest() {
        UnsupportedStatusException e = new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    public void handleInvalidBookingExceptionTest() {
        InvalidBookingException e = new InvalidBookingException("недопустимое бронирование 404: ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "недопустимое бронирование 404: ");
    }

    @Test
    public void handleCommentExceptionTest() {
        CommentException e = new CommentException("невозможно оставить комментарий 400: ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "невозможно оставить комментарий 400: ");
    }
}