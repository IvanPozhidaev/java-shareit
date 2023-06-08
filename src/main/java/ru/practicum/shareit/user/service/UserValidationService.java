package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.InvalidUserException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

@Service
@AllArgsConstructor
public class UserValidationService {

    public static final String USER_NOT_FOUND = "Не найден пользователь с id: ";
    public static final String INVALID_NAME = "Некорректное имя пользователя с id: ";
    public static final String INVALID_EMAIL = "Некорректная почта пользователя с id: ";

    public void validateUser(User user) {
        checkUsername(user);
        validateEmail(user);
    }

    public void validateUserId(long userId) {
        if (userId <= 0) throw new UserNotFoundException(USER_NOT_FOUND + userId);
    }

    private void checkUsername(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) throw new InvalidUserException(INVALID_NAME + user.getId());
    }

    private void validateEmail(User user) {
        boolean valid = EmailValidator.getInstance().isValid(user.getEmail());
        if (!valid) throw new InvalidUserException(INVALID_EMAIL + user.getId());
    }
}