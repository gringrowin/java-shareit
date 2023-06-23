package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.ItemResponseBookingDto;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

public class ItemMapper {

    public static ItemInputDto toItemDto(Item item) {
        return new ItemInputDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

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
        return itemOutputDto;
    }

    public static Item toItem(ItemInputDto itemInputDto, User user) {
        return new Item(
                itemInputDto.getId(),
                itemInputDto.getName(),
                itemInputDto.getDescription(),
                itemInputDto.getAvailable(),
                user
        );
    }
}
