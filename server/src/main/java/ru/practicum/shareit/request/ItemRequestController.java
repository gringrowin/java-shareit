package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestOutputDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody ItemRequestInputDto itemRequestInputDto) {
        log.info("ItemRequestController.create: {} - Started", itemRequestInputDto);
        ItemRequestOutputDto itemRequestOutputDto = itemRequestService.create(userId, itemRequestInputDto);
        log.info("ItemRequestController.create: {} - Finished", itemRequestOutputDto);
        return itemRequestOutputDto;
    }

    @GetMapping
    public List<ItemRequestOutputDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("ItemRequestController.getItemRequests: {} - Started", userId);
        List<ItemRequestOutputDto> itemRequests = itemRequestService.getItemRequests(userId);
        log.info("ItemRequestController.getItemRequests: {} - Finished", itemRequests.size());
        return itemRequests;
    }

    @GetMapping("/all")
    public List<ItemRequestOutputDto> getItemRequestsByOtherUsers(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        log.info("ItemRequestController.getItemRequestsByAnotherUsers: {} - Started", userId);
        List<ItemRequestOutputDto> itemRequests = itemRequestService.getItemRequestsByOtherUsers(userId, from, size);
        log.info("ItemRequestController.getItemRequestsByAnotherUsers: {} - Finished", itemRequests.size());
        return itemRequests;
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestOutputDto getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable("itemRequestId") Long itemRequestId) {
        log.info("ItemRequestController.getItem: {} id - Started", itemRequestId);
        ItemRequestOutputDto itemRequestOutputDto = itemRequestService.getItemRequestById(itemRequestId, userId);
        log.info("ItemRequestController.getItem: {} - Finished", itemRequestOutputDto);
        return itemRequestOutputDto;
    }


}
