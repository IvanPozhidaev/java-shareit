package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.InvalidItemException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.dao.UserDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemValidationService {

    public static final String OWNER_NOT_STATED = "Не указан владелец вещи";
    public static final String INCORRECT_ID = "Некорректное значение id: ";
    public static final String EMPTY_NAME = "Название вещи не указано или пустое";
    public static final String OWNER_NOT_FOUND = "Не найден владелец c id: ";
    public static final String ITEM_STATUS_NOT_STATED = "Не указан статус доступности предмета";
    public static final String EMPTY_DESCRIPTION = "Описание вещи не указано или пустое";

    private final UserDao userDao;

    public void validateItem(Item item) {
        checkOwnerNotNull(item);

        if (item.getAvailable() == null) {
            throw new InvalidItemException(ITEM_STATUS_NOT_STATED);
        }

        String itemName = item.getName();
        if (itemName == null || itemName.isBlank()) {
            throw new InvalidItemException(EMPTY_NAME);
        }

        String itemDescription = item.getDescription();
        if (itemDescription == null || itemDescription.isBlank()) {
            throw new InvalidItemException(EMPTY_DESCRIPTION);
        }

        boolean ownerExists = checkIsOwnerExists(item.getOwner());
        if (!ownerExists) {
            throw new OwnerNotFoundException(OWNER_NOT_FOUND + item.getOwner());
        }
    }

    public void checkOwnerNotNull(Item item) {
        if (item.getOwner() == null) throw new InvalidItemException(OWNER_NOT_STATED);
    }

    public void validateItemId(Long itemId) {
        if (itemId == null || itemId <= 0) throw new ItemNotFoundException(INCORRECT_ID);
    }

    public void validateUserId(Long userId) {
        if (userId == null || userId <= 0 || !checkIsOwnerExists(userId)) {
            throw new OwnerNotFoundException(OWNER_NOT_FOUND + userId);
        }
    }

    private boolean checkIsOwnerExists(long ownerId) {
        List<User> users = userDao.findAllUsers();
        List<User> result = users.stream().filter(user -> user.getId() == ownerId).collect(Collectors.toList());
        return result.size() > 0;
    }
}