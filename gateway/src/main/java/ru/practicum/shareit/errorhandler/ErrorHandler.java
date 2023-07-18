package ru.practicum.shareit.errorhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(MethodArgumentNotValidException e) {
        log.warn("Ошибка валидации", e);
        return new ErrorResponse("Ошибка валидации 400: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(IllegalArgumentException e) {
        log.warn("Недопустимое значение", e);
        return new ErrorResponse("Передано недопустимое значение 400: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Throwable e) {
        log.warn("Непредвиденная ошибка сервера", e);
        return new ErrorResponse("непредвиденная ошибка сервера 500: ", e.getMessage());
    }
}
