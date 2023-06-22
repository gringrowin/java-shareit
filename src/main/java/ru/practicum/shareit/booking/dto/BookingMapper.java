package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, Item item, User booker, BookingState status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        booking.setEnd(bookingDto.getEnd());
        booking.setStart(bookingDto.getStart());
        return booking;
    }

    public static ItemResponseBookingDto toItemResponseBookingDto(Booking booking) {
                return ItemResponseBookingDto.builder()
                        .id(booking.getId())
                        .bookerId(booking.getBooker().getId())
                        .status(booking.getStatus())
                        .build();
    }
}
