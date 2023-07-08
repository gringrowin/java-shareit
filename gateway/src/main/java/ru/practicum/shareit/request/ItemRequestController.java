package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemRequestInputDto itemRequestInputDto) {
        log.info("Gateway.ItemRequestController.create: {} - Started", itemRequestInputDto);
        return itemRequestClient.create(userId, itemRequestInputDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway.ItemRequestController.getItemRequests: {} - Started", userId);
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("all")
    public ResponseEntity<Object> getItemRequestsByOtherUsers(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(name = "size", required = false, defaultValue = "20") @Min(1) @Max(30) Integer size) {
        log.info("Gateway.ItemRequestController.getItemRequestsByAnotherUsers: {} - Started", userId);
        return itemRequestClient.getItemRequestsByOtherUsers(userId, from, size);
    }

    @GetMapping("{itemRequestId}")
    public ResponseEntity<Object> getItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemRequestId") Long itemRequestId) {
        log.info("Gateway.ItemRequestController.getItem: {} id - Started", itemRequestId);
        return itemRequestClient.getItemRequest(userId, itemRequestId);
    }
}
