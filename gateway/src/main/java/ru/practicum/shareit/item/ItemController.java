package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.MarkerItemDto;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId) {
        log.info("Gateway.ItemController.getItem: {} {} - ", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Validated(MarkerItemDto.OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("Gateway.ItemController.create: {} {} ", itemDto, userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId,
            @Validated(MarkerItemDto.OnUpdate.class) @RequestBody ItemDto itemDto) {
            log.info("Gateway.ItemController.update: {} {} ", itemId, itemDto);
        return itemClient.update(userId, itemId, itemDto);
    }

    @DeleteMapping("{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Gateway.ItemController.delete: {} - Started", userId);
        itemClient.delete(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway.ItemController.delete: {} ", userId);
        return itemClient.getItemsByUserId(userId);
    }

    @GetMapping("search")
    public ResponseEntity<Object> search(
            @RequestParam(name = "text") String text) {
        log.info("Gateway.ItemController.searchItems: {} - Started", text);
        return itemClient.search(text);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody CommentDto commentDto) {
        log.info("Gateway.ItemController.addComment: {} {} {} - Started", itemId, commentDto, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
