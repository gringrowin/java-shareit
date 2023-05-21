package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public ItemDto getItem(Long id) {
        log.info("ItemService.getItem id : {} - Started", id);
        Item item = itemStorage.getItem(id);
        if (item == null) {
            throw new ItemNotFoundException(String.format("Вещь с ID : %s не найдена", id));
        }
        log.info("ItemService.getItem id : {} - Finished", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto create(Long userId, Item item) {
        log.info("ItemService.create: {} - Started", itemDto);
        item.setOwner(userService.getUser(userId));

        return null;
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        return null;
    }

    @Override
    public void deleteItem(Long id) {

    }
}
