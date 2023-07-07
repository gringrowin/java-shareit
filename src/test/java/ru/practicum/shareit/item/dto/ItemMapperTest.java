package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.ItemResponseBookingDto;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    private ItemOutputDto itemOutputDto;

    private ItemDto itemDto;

    private Item item;

    private  CommentDto commentDto;

    private ItemResponseBookingDto lastBooking;

    private ItemResponseBookingDto nextBooking;

    private User user;

    private ItemRequest itemRequest;

    @BeforeEach
    void setTestData() {
        user = new User();
        lastBooking = new ItemResponseBookingDto();
        nextBooking = new ItemResponseBookingDto();
        commentDto = new CommentDto();

        itemRequest = new ItemRequest();
            itemRequest.setId(1L);

        item = new Item();
            item.setId(1L);
            item.setName("name");
            item.setDescription("description");
            item.setAvailable(true);
            item.setOwner(user);
            item.setItemRequest(itemRequest);

        itemDto = new ItemDto();
            itemDto.setId(1L);
            itemDto.setName("name");
            itemDto.setDescription("description");
            itemDto.setAvailable(true);
            itemDto.setRequestId(1L);

        itemOutputDto = new ItemOutputDto();
        itemOutputDto.setId(1L);
        itemOutputDto.setName("name");
        itemOutputDto.setDescription("description");
        itemOutputDto.setAvailable(true);
        itemOutputDto.setLastBooking(lastBooking);
        itemOutputDto.setNextBooking(nextBooking);
        itemOutputDto.setComments(List.of(commentDto));
        itemOutputDto.setRequestId(1L);
    }

    @Test
    void toItemOutputDtoWhenOwnerIdEqualsUserId() {
        user.setId(1L);

        ItemOutputDto itemOutputDtoTest =
                ItemMapper.toItemOutputDto(
                        item,
                        1L,
                        lastBooking,
                        nextBooking,
                        List.of(commentDto));

        assertEquals(itemOutputDtoTest, itemOutputDto);
    }

    @Test
    void toItemOutputDtoWhenOwnerIdNotEqualsUserId() {

        ItemOutputDto itemOutputDtoTest =
                ItemMapper.toItemOutputDto(
                        item,
                        1L,
                        lastBooking,
                        nextBooking,
                        List.of(commentDto));

        assertNull(itemOutputDtoTest.getLastBooking());
        assertNull(itemOutputDtoTest.getNextBooking());
        assertEquals(itemOutputDtoTest.getId(), itemOutputDto.getId());
    }

    @Test
    void toItem() {
        Item itemTest = ItemMapper.toItem(itemDto, user, itemRequest);
        assertEquals(itemTest, item);
    }

    @Test
    void toItemDto() {
        ItemDto itemDtoTest = ItemMapper.toItemDto(item);
        assertEquals(itemDtoTest, itemDto);
    }
}