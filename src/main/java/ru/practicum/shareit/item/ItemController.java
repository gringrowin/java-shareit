package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.MarkerItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

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
                          @Validated(MarkerItemDto.OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("ItemController.create: {} - Started", itemDto);
        itemDto = itemService.create(userId, itemDto);
        log.info("ItemController.create: {} - Finished", itemDto);
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable("itemId") Long itemId,
                          @Validated(MarkerItemDto.OnUpdate.class) @RequestBody ItemDto itemDto) {
        log.info("ItemController.update: {} {} - Started", itemId, itemDto);
        itemDto = itemService.update(userId, itemId, itemDto);
        log.info("ItemController.update: {} - Finished", itemDto);
        return itemDto;
    }

    @DeleteMapping("/{itemId}")
    public Long delete(@PathVariable("itemId") Long id) {
        log.info("ItemController.delete: {} - Started", id);
        itemService.deleteItem(id);
        log.info("ItemController.delete: {} - Finished", id);
        return id;
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("ItemController.delete: {} - Started", userId);
        List<ItemDto> items = itemService.getItemsByUserId(userId);
        log.info("ItemController.delete: {} - Finished", items.size());
        return items;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(name = "text") String text) {
        log.info("ItemController.searchItems: {} - Started", text);
        List<ItemDto> items = itemService.searchItems(text);
        log.info("ItemController.searchItems: {} - Finished", items.size());
        return items;
    }
}
