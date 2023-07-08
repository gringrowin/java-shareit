package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestInputDto inputDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
                itemRequest.setDescription(inputDto.getDescription());
                itemRequest.setRequester(user);
                itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static ItemRequestOutputDto toItemRequestOutputDto(ItemRequest itemRequest,
                                                              List<ItemDto> items) {
        ItemRequestOutputDto itemRequestOutputDto = new ItemRequestOutputDto();
                itemRequestOutputDto.setId(itemRequest.getId());
                itemRequestOutputDto.setDescription(itemRequest.getDescription());
                itemRequestOutputDto.setCreated(itemRequest.getCreated());
                itemRequestOutputDto.setItems(items);
        return itemRequestOutputDto;
    }
}
