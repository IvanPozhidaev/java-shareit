package ru.practicum.shareit.item.storage.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {

    Item createItem(Item item);

    Item updateItem(Item item);

    Item findItemById(Long itemId);

    List<Item> findAllItems(Long userId);

    List<Item> findItemsByRequest(String text);
}