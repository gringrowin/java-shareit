package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.ItemResponseBookingDto;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
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
        itemOutputDto.setRequestId(item.getItemRequestId());
        return itemOutputDto;
    }

    public static Item toItem(ItemDto itemDto, User user) {

        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemDto.getRequestId()
        );
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
            itemDto.setId(item.getId());
            itemDto.setName(item.getName());
            itemDto.setDescription(item.getDescription());
            itemDto.setAvailable(item.getAvailable());
            itemDto.setRequestId(item.getItemRequestId());
        return itemDto;
    }
}
