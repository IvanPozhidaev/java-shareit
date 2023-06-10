package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.dao.ItemDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.dao.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    public static final String OWNER_NOT_FOUND = "Не найден владелец c id: ";
    public static final int MIN_SEARCH_REQUEST_LENGTH = 3;

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public Item createItem(Item item) {
        boolean ownerExists = isOwnerExists(item.getOwner());
        if (!ownerExists) {
            throw new OwnerNotFoundException(OWNER_NOT_FOUND + item.getOwner());
        }
        return itemDao.createItem(item);
    }

    @Override
    public Item updateItem(Item item) {
        return itemDao.updateItem(item);
    }

    @Override
    public Item findItemById(Long itemId) {
        return itemDao.findItemById(itemId);
    }

    @Override
    public List<Item> findAllItems(Long userId) {
        return itemDao.findAllItems(userId);
    }

    @Override
    public List<Item> findItemsByRequest(String text) {
        if (text == null || text.isBlank() || text.length() <= MIN_SEARCH_REQUEST_LENGTH) {
            return new ArrayList<>();
        }
        return itemDao.findItemsByRequest(text);
    }

    private boolean isOwnerExists(long ownerId) {
        List<User> users = userDao.findAllUsers();
        List<User> result = users.stream().filter(user -> user.getId() == ownerId).collect(Collectors.toList());
        return result.size() > 0;
    }
}