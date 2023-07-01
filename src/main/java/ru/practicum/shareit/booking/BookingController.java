package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public Booking addBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                              @Valid @RequestBody BookingDto bookingDto) {
        log.info("BookingController.addBooking: {} - Started", bookingDto);
        Booking booking = service.addBooking(bookerId, bookingDto);
        log.info("BookingController.addBooking: {} - Finished", booking);
        return booking;
    }

    @PatchMapping("/{bookingId}")
    public Booking approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                           @RequestParam Boolean approved,
                                           @PathVariable Long bookingId) {
        log.info("BookingController.approveBooking: {}, {} - Started", ownerId, bookingId);
        Booking booking = service.approveBooking(ownerId, approved, bookingId);
        log.info("BookingController.approveBooking: {} - Finished", booking);
        return booking;
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @PathVariable Long bookingId) {
        log.info("BookingController.getBookingById: {}, {} - Started", bookerId, bookingId);
        Booking booking = service.getBookingById(bookerId, bookingId);
        log.info("BookingController.getBookingById: {} - Finished", booking);
        return booking;
    }

    @GetMapping
    public List<Booking> getAllBookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestParam(name = "from", required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(name = "size", required = false, defaultValue = "20") @Min(1) @Max(30) Integer size) {
        log.info("BookingController.getAllBookingsByBooker: {}, {} - Started", bookerId, state);
        List<Booking> bookings = service.getAllBookingsByBooker(bookerId, state, from, size);
        log.info("BookingController.getAllBookingsByBooker: {} - Finished", bookings.size());
        return bookings;
    }

    @GetMapping("/owner")
    public List<Booking> getAllBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestParam(name = "from", required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(name = "size", required = false, defaultValue = "20") @Min(1) @Max(30) Integer size) {
        log.info("BookingController.getAllBookingsByOwner: {}, {} - Started", bookerId, state);
        List<Booking> bookings = service.getAllBookingsByOwner(bookerId, state, from, size);
        log.info("BookingController.getAllBookingsByOwner: {} - Finished", bookings.size());
        return bookings;
    }
}
