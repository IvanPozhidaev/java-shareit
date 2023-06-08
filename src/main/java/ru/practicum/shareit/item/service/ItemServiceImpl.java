package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.dao.ItemDao;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final ItemValidationService validationService;

    @Override
    public Item createItem(Item item) {
        validationService.validateItem(item);
        return itemDao.createItem(item);
    }

    @Override
    public Item updateItem(Item item) {
        validationService.checkOwnerNotNull(item);
        return itemDao.updateItem(item);
    }

    @Override
    public Item findItemById(Long itemId) {
        validationService.validateItemId(itemId);
        return itemDao.findItemById(itemId);
    }

    @Override
    public List<Item> findAllItems(Long userId) {
        validationService.validateUserId(userId);
        return itemDao.findAllItems(userId);
    }

    @Override
    public List<Item> findItemsByRequest(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemDao.findItemsByRequest(text);
    }
}