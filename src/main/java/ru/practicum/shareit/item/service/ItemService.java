package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemOutputDto getItem(Long id, Long userId);

    Item findById(Long id);

    ItemOutputDto create(Long userId, ItemInputDto itemInputDto);

    ItemOutputDto update(Long userId, Long itemId, ItemInputDto itemInputDto);

    void deleteItem(Long id, Long userId);

    List<ItemOutputDto> getItemsByUserId(Long userId);

    List<ItemOutputDto> searchItems(String text);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);
}
