package ru.practicum.shareit.user.storage.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.EmailConflictException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.dao.UserDao;

import java.util.*;

@Repository
public class InMemoryUserDaoImpl implements UserDao {
    public static final String USER_NOT_FOUND = "не найден пользователь с id: ";
    public static final String EMAIL_CONFLICT = "email уже используется другим пользователем: ";

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long currentId = 1;

    @Override
    public User createUser(User user) {
        checkEmailIsAlreadyUsed(user.getEmail());
        user.setId(generateId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User updateUser(long userId, User user) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(USER_NOT_FOUND + userId);
        }
        User oldEntry = users.get(userId);

        if (user.getName() != null) {
            oldEntry.setName(user.getName());
        }

        String oldEmail = users.get(userId).getEmail();
        String newEmail = user.getEmail();
        if (newEmail != null && !oldEmail.equals(newEmail)) {
            tryRefreshUserEmail(oldEmail, newEmail);
            oldEntry.setEmail(newEmail);
        }
        return oldEntry;
    }

    @Override
    public User findUserById(long userId) {
        if (!users.containsKey(userId)) throw new UserNotFoundException(USER_NOT_FOUND + userId);
        return users.get(userId);
    }

    @Override
    public void deleteUserById(long userId) {
        if (!users.containsKey(userId)) throw new UserNotFoundException(USER_NOT_FOUND + userId);
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void checkEmailIsAlreadyUsed(String email) {
        if (emails.contains(email)) throw new EmailConflictException(EMAIL_CONFLICT + email);
    }

    private void tryRefreshUserEmail(String oldEmail, String newEmail) {
        emails.remove(oldEmail);
        if (emails.contains(newEmail)) {
            emails.add(oldEmail);
            throw new EmailConflictException(EMAIL_CONFLICT + newEmail);
        }
        emails.add(newEmail);
    }

    private long generateId() {
        return currentId++;
    }
}