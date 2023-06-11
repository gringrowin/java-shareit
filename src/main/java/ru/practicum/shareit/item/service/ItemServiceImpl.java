package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemDto getItem(Long id) {
        log.info("ItemService.getItem id : {} - Started", id);
        Item item = findById(id);
        log.info("ItemService.getItem id : {} - Finished", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("ItemService.create: {} - Started", itemDto);
        User user = userService.findById(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        item = itemRepository.save(item);
        itemDto = ItemMapper.toItemDto(item);
        log.info("ItemService.create: {} - Finished", itemDto);
        return itemDto;
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        log.info("ItemService.update: {} {} {} - Started", userId, itemId,  itemDto);
        Item item = findById(itemId);
        User user = userService.findById(userId);
        if (!user.equals((item.getOwner()))) {
            throw new ItemNotFoundException(
                    String.format("Пользователь с ID %s не является владельцем вещи", userId)
            );
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        item = itemRepository.save(item);
        log.info("ItemService.update: {} - Finished", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public void deleteItem(Long id) {
        log.info("ItemService.deleteItem: {} - Started", id);
        itemRepository.delete(findById(id));
        log.info("ItemService.deleteItem: {} - Finished", id);
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        log.info("ItemService.getItemsByUserId: {} - Started", userId);
        List<Item> itemsByUserId = itemRepository.findByOwnerId(userId);
        log.info("ItemService.getItemsByUserId: {} - Finished", itemsByUserId.size());
        return itemsByUserId.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("ItemService.searchItems: {} - Started", text);
        List<Item> findItems = itemRepository.search(text);
        log.info("ItemService.searchItems: {} - Finished", findItems.size());
        return findItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item findById(Long id) {
        log.info("ItemService.findById id : {}", id);
        return itemRepository.findById(id)
                .orElseThrow(
                        () -> new ItemNotFoundException(
                                String.format("Вещь с ID : %s не найдена", id))
                );
    }
}
