package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
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
    public ItemOutputDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable("itemId") Long itemId) {
        log.info("ItemController.getItem: {} id - Started", itemId);
        ItemOutputDto itemOutputDto = itemService.getItem(itemId, userId);
        log.info("ItemController.getItem: {} - Finished", itemOutputDto);
        return itemOutputDto;
    }

    @PostMapping
    public ItemOutputDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestBody ItemDto itemDto) {
        log.info("ItemController.create: {} - Started", itemDto);
        ItemOutputDto itemOutputDto = itemService.create(userId, itemDto);
        log.info("ItemController.create: {} - Finished", itemOutputDto);
        return itemOutputDto;
    }

    @PatchMapping("/{itemId}")
    public ItemOutputDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable("itemId") Long itemId,
                               @RequestBody ItemDto itemDto) {
        log.info("ItemController.update: {} {} - Started", itemId, itemDto);
        ItemOutputDto itemOutputDto = itemService.update(userId, itemId, itemDto);
        log.info("ItemController.update: {} - Finished", itemOutputDto);
        return itemOutputDto;
    }

    @DeleteMapping("/{itemId}")
    public Long delete(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long id) {
        log.info("ItemController.delete: {} - Started", id);
        itemService.deleteItem(id, userId);
        log.info("ItemController.delete: {} - Finished", id);
        return id;
    }

    @GetMapping
    public List<ItemOutputDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("ItemController.delete: {} - Started", userId);
        List<ItemOutputDto> items = itemService.getItemsByUserId(userId);
        log.info("ItemController.delete: {} - Finished", items.size());
        return items;
    }

    @GetMapping("/search")
    public List<ItemOutputDto> searchItems(@RequestParam(name = "text") String text) {
        log.info("ItemController.searchItems: {} - Started", text);
        List<ItemOutputDto> items = itemService.searchItems(text);
        log.info("ItemController.searchItems: {} - Finished", items.size());
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                                 @RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody CommentDto commentDto) {
        log.info("ItemController.addComment: {} {} {} - Started", itemId, commentDto, userId);
        CommentDto result = itemService.addComment(itemId, userId, commentDto);
        log.info("ItemController.addComment: {} - Finished", result);
        return result;
    }
}
