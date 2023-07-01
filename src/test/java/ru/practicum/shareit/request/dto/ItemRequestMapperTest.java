package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    ItemRequestInputDto itemRequestInputDto;

    ItemRequestOutputDto itemRequestOutputDto;

    ItemRequest itemRequest;

    User user;

    @BeforeEach
    void setTestData() {
        itemRequestInputDto = new ItemRequestInputDto();
            itemRequestInputDto.setDescription("description");

        user = new User();

        itemRequestOutputDto = new ItemRequestOutputDto();
            itemRequestOutputDto.setId(1L);
            itemRequestOutputDto.setDescription("description");
            itemRequestOutputDto.setCreated(LocalDateTime.MAX);
            itemRequestOutputDto.setItems(List.of(new ItemDto()));

        itemRequest = new ItemRequest();
            itemRequest.setId(1L);
            itemRequest.setDescription("description");
            itemRequest.setRequester(user);
            itemRequest.setCreated(LocalDateTime.MAX);
    }

    @Test
    void toItemRequestTest() {
        ItemRequest itemRequest1 = ItemRequestMapper.toItemRequest(itemRequestInputDto, user);
        itemRequest1.setId(1L);
        assertEquals(itemRequest1.getRequester(), itemRequest.getRequester());
        assertEquals(itemRequest1.getId(), itemRequest.getId());
        assertEquals(itemRequest1.getDescription(), itemRequest.getDescription());
    }

    @Test
    void toItemRequestOutputDtoTest() {
        ItemRequestOutputDto itemRequestOutputDto1 =
                ItemRequestMapper.toItemRequestOutputDto(itemRequest, List.of(new ItemDto()));
        assertEquals(itemRequestOutputDto1.getId(), itemRequestOutputDto.getId());
        assertEquals(itemRequestOutputDto1.getDescription(), itemRequestOutputDto.getDescription());
        assertEquals(itemRequestOutputDto1.getItems(), itemRequestOutputDto.getItems());
    }


}