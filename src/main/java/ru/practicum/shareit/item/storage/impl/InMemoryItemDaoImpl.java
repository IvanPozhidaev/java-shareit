package ru.practicum.shareit.item.storage.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exception.DeniedAccessException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.dao.ItemDao;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemDaoImpl implements ItemDao {

    public static final String ITEM_NOT_FOUND = "Не найдена вещь с id: ";
    public static final String ACCESS_DENIED = "Пользователь не является владельцем вещи";

    private final Map<Long, Item> items = new HashMap<>();
    private Map<Long, List<Item>> userItems = new HashMap<>();
    private long currentId = 1;

    @Override
    public Item createItem(Item item) {
        long id = generateId();
        item.setId(id);
        items.put(id, item);
        List<Item> userItemList = userItems.computeIfAbsent(item.getOwner(), k -> new ArrayList<>());
        userItemList.add(item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        long itemId = item.getId();

        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException(ITEM_NOT_FOUND + itemId);
        }

        Item updatedItem = items.get(itemId);

        if (!updatedItem.getOwner().equals(item.getOwner())) {
            throw new DeniedAccessException(ACCESS_DENIED +
                    "userId: " + item.getOwner() + ", itemId: " + itemId);
        }

        refreshItem(updatedItem, item);
        return updatedItem;
    }

    @Override
    public Item findItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException(ITEM_NOT_FOUND + itemId);
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> findAllItems(Long userId) {
        return userItems.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public List<Item> findItemsByRequest(String text) {
        String wantedItem = text.toLowerCase();
        return items.values()
                .stream()
                .filter(item -> {
                    String itemName = item.getName().toLowerCase();
                    String itemDescription = item.getDescription().toLowerCase();
                    return itemName.contains(wantedItem) || itemDescription.contains(wantedItem);
                })
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    private long generateId() {
        return currentId++;
    }

    private void refreshItem(Item oldEntry, Item newEntry) {
        String name = newEntry.getName();
        if (name != null) {
            oldEntry.setName(name);
        }

        String description = newEntry.getDescription();
        if (description != null) {
            oldEntry.setDescription(description);
        }

        Boolean available = newEntry.getAvailable();
        if (available != null) {
            oldEntry.setAvailable(available);
        }
    }
}