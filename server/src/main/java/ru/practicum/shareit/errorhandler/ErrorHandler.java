package ru.practicum.shareit.errorhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(OwnerNotFoundException e) {
        log.warn("Не найден user ", e);
        return new ErrorResponse("Не найден владелец вещи ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handle(DeniedAccessException e) {
        log.warn("Отказ доступа", e);
        return new ErrorResponse("Отказано в доступе ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(MethodArgumentNotValidException e) {
        log.warn("Ошибка валидации", e);
        return new ErrorResponse("Ошибка валидации 400: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(NoSuchElementException e) {
        log.warn("Не найден item", e);
        return new ErrorResponse("Ошибка поиска элемента 404: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(UnavailableBookingException e) {
        log.warn("Ошибка бронирования", e);
        return new ErrorResponse("Ошибка бронирования 400: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(IllegalArgumentException e) {
        log.warn("Недопустимое значение", e);
        return new ErrorResponse("Передано недопустимое значение 400: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(UnsupportedStatusException e) {
        log.warn("Неподдерживаемый статус", e);
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(InvalidBookingException e) {
        log.warn("Недопустимое бронирование", e);
        return new ErrorResponse("недопустимое бронирование 404: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(CommentException e) {
        log.warn("Ошибка комментария", e);
        return new ErrorResponse("невозможно оставить комментарий 400: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Throwable e) {
        e.printStackTrace();
        log.warn("Непредвиденная ошибка сервера", e);
        return new ErrorResponse("непредвиденная ошибка сервера 500: ", e.getMessage());
    }
}