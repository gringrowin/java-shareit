package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.ItemResponseBookingDto;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

public class ItemMapper {

    public static ItemOutputDto toItemOutputDto(
            Item item,
            Long userId,
            ItemResponseBookingDto lastBooking,
            ItemResponseBookingDto nextBooking,
            List<CommentDto> comments
    )    {

        ItemOutputDto itemOutputDto = ItemOutputDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .build();

        if (Objects.equals(item.getOwner().getId(), userId)) {
            itemOutputDto.setLastBooking(lastBooking);
            itemOutputDto.setNextBooking(nextBooking);
        }
        if (item.getItemRequest() != null) {
            itemOutputDto.setRequestId(item.getItemRequest().getId());
        }
        return itemOutputDto;
    }

    public static Item toItem(ItemDto itemDto, User user, ItemRequest itemRequest) {
        Item item = new Item();
            item.setId(itemDto.getId());
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setAvailable(itemDto.getAvailable());
            item.setOwner(user);
            item.setItemRequest(itemRequest);

        return item;
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
            itemDto.setId(item.getId());
            itemDto.setName(item.getName());
            itemDto.setDescription(item.getDescription());
            itemDto.setAvailable(item.getAvailable());
            itemDto.setRequestId(item.getItemRequest().getId());
        return itemDto;
    }
}
