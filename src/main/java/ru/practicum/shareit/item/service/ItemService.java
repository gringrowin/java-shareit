package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {
    ItemDto getItem(Long id);

    ItemDto create(Long userId, Item item);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    void deleteItem(Long id);
}
