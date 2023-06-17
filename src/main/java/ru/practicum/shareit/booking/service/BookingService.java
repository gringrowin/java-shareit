package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking addBooking(Long bookerId, BookingDto bookingDto);

    Booking approveBooking(Long ownerId, Boolean approved, Long bookingId);

    Booking getBookingById(Long bookerId, Long bookingId);

    List<Booking> getAllBookingsByBookerAndState(Long bookerId, BookingState state);

    List<Booking> getAllBookingsByOwnerAndState(Long bookerId, BookingState state);
}
