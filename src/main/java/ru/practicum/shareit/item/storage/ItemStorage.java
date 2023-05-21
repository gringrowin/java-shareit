package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

public interface ItemStorage {
    Item getItem(Long id);
}
