package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestOutputDto getItemRequestById(Long itemRequestId, Long userId);

    List<ItemRequestOutputDto> getItemRequestsByOtherUsers(Long userId, Integer from, Integer size);

    List<ItemRequestOutputDto> getItemRequests(Long userId);

    ItemRequestOutputDto create(Long userId, ItemRequestInputDto itemRequestInputDto);

    ItemRequest findById(Long id);
}
