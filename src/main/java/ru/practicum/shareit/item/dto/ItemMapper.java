package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
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
            Booking lastBooking,
            Booking nextBooking,
            List<CommentDto> comments
    )    {

        ItemOutputDto itemOutputDto = ItemOutputDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        if (Objects.equals(item.getOwner().getId(), userId)) {
            if (lastBooking != null && lastBooking.getStatus() != BookingState.REJECTED) {
                ItemResponseBookingDto lastBookingDto = ItemResponseBookingDto.builder()
                        .id(lastBooking.getId())
                        .bookerId(lastBooking.getBooker().getId())
                        .status(lastBooking.getStatus())
                        .build();
                itemOutputDto.setLastBooking(lastBookingDto);
            }

            if (nextBooking != null && nextBooking.getStatus() != BookingState.REJECTED) {
                ItemResponseBookingDto nextBookingDto = ItemResponseBookingDto.builder()
                        .id(nextBooking.getId())
                        .bookerId(nextBooking.getBooker().getId())
                        .status(nextBooking.getStatus())
                        .build();
                itemOutputDto.setNextBooking(nextBookingDto);
            }
        }
        itemOutputDto.setComments(comments);
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
