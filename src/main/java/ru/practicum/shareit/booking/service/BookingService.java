package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking addBooking(Long bookerId, BookingDto bookingDto);

    Booking approveBooking(Long ownerId, Boolean approved, Long bookingId);

    Booking getBookingById(Long bookerId, Long bookingId);

    List<Booking> getAllBookingsByBooker(Long userId, BookingState state, Integer from, Integer size);

    List<Booking> getAllBookingsByOwner(Long userId, BookingState state, Integer from, Integer size);
}
