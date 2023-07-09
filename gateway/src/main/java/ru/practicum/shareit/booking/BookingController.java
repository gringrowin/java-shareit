package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> addBooking(
			@RequestHeader("X-Sharer-User-Id") Long bookerId,
			@Valid @RequestBody BookingDto bookingDto) {
		log.info("Gateway.BookingController.addBooking: {} - Started", bookingDto);
		return bookingClient.create(bookingDto, bookerId);
	}

	@PatchMapping("{bookingId}")
	public ResponseEntity<Object> approveBooking(
			@RequestHeader("X-Sharer-User-Id") Long ownerId,
			@RequestParam Boolean approved,
			@PathVariable Long bookingId) {
		log.info("Gateway.BookingController.approveBooking: {}, {}", ownerId, bookingId);
		return bookingClient.approve(bookingId, ownerId, approved);
	}

	@GetMapping("{bookingId}")
	public ResponseEntity<Object> getBookingById(
			@RequestHeader("X-Sharer-User-Id") Long bookerId,
			@PathVariable Long bookingId) {
		log.info("Gateway.BookingController.getBookingById: {}, {} - Started", bookerId, bookingId);
		return bookingClient.findById(bookingId, bookerId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBookingsByBooker(
			@RequestHeader("X-Sharer-User-Id") Long bookerId,
			@RequestParam(required = false, defaultValue = "ALL") BookingState state,
			@RequestParam(name = "from", required = false, defaultValue = "0") @Min(0) Integer from,
			@RequestParam(name = "size", required = false, defaultValue = "20") @Min(1) @Max(30) Integer size) {
		log.info("Gateway.BookingController.getAllBookingsByBooker: {}, {} - Started", bookerId, state);
		return bookingClient.findAllByUser(bookerId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsByOwner(
			@RequestHeader("X-Sharer-User-Id") Long bookerId,
			@RequestParam(required = false, defaultValue = "ALL") BookingState state,
			@RequestParam(name = "from", required = false, defaultValue = "0") @Min(0) Integer from,
			@RequestParam(name = "size", required = false, defaultValue = "20") @Min(1) @Max(30) Integer size) {
		log.info("Gateway.BookingController.getAllBookingsByOwner: {}, {} - Started", bookerId, state);
		return bookingClient.findAllByOwner(bookerId, state, from, size);
	}
}
