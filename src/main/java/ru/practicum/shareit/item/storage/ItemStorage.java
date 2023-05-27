package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item getItem(Long id);

    Item addItem(Item item);

    Item update(Item item);

    void delete(Long id);

    List<Item> getItemsByUserId(Long userId);

    List<Item> searchItems(String text);
}
