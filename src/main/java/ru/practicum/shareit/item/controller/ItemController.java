package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validationmarkers.Create;
import ru.practicum.shareit.validationmarkers.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    public static final int MIN_ID_VALUE = 1;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String NULL_ITEM_ID = "itemID is null";
    public static final String NULL_USER_ID= "userID is null";

    private final ItemService itemService;
    private final ItemMapper mapper;

    @PostMapping
    public ItemDto createItem(@Validated({Create.class})
                              @RequestBody ItemDto itemDto,
                              @NotNull(message = (NULL_ITEM_ID))
                              @Min(MIN_ID_VALUE)
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        Item item = mapper.toModel(itemDto, userId);
        return mapper.toDto(itemService.createItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated({Update.class})
                              @RequestBody ItemDto itemDto,
                              @NotNull(message = NULL_ITEM_ID)
                              @Min(MIN_ID_VALUE)
                              @PathVariable Long itemId,
                              @NotNull(message = NULL_USER_ID)
                              @Min(MIN_ID_VALUE)
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        Item item = mapper.toModel(itemDto, userId);
        item.setId(itemId);
        return mapper.toDto(itemService.updateItem(item));
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@NotNull(message = NULL_ITEM_ID)
                                @Min(MIN_ID_VALUE)
                                @PathVariable Long itemId) {
        return mapper.toDto(itemService.findItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> findAllItems(@NotNull(message = NULL_USER_ID)
                                      @Min(MIN_ID_VALUE)
                                      @RequestHeader(USER_ID_HEADER) Long userId) {
        List<Item> userItems = itemService.findAllItems(userId);
        return mapper.mapItemListToItemDtoList(userItems);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByRequest(@RequestParam String text) {
        List<Item> foundItems = itemService.findItemsByRequest(text);
        return mapper.mapItemListToItemDtoList(foundItems);
    }
}