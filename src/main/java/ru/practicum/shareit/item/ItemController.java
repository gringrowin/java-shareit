package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable("itemId") Long id) {
        log.info("ItemController.getItem: {} id - Started", id);
        ItemDto itemDto = itemService.getItem(id);
        log.info("ItemController.getItem: {} - Finished", itemDto);
        return itemDto;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody Item item) {
        log.info("ItemController.create: {} - Started", item);
        ItemDto itemDto = itemService.create(userId, item);
        log.info("ItemController.create: {} - Finished", itemDto);
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable("itemId") Long itemId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("ItemController.update: {} {} - Started", itemId, itemDto);
        itemDto = itemService.update(userId, itemId, itemDto);
        log.info("ItemController.update: {} - Finished", itemDto);
        return itemDto;
    }

    @DeleteMapping("/{itemId}")
    public Long delete (@PathVariable("itemId") Long id) {
        log.info("ItemController.delete: {} - Started", id);
        itemService.deleteItem(id);
        log.info("ItemController.delete: {} - Finished", id);
        return id;
    }
}
