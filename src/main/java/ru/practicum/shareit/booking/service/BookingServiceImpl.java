package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public BookingServiceImpl(BookingRepository repository,
                              ItemService itemService,
                              UserService userService) {
        this.bookingRepository = repository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    public Booking addBooking(Long bookerId, BookingDto bookingDto) {
        log.info("BookingService.addBooking: {} - Started", bookingDto);
        Item item = itemService.findById(bookingDto.getItemId());
        User user = userService.findById(bookerId);

        if (item.getOwner().getId().equals(bookerId)) {
            throw new UserNotFoundException("Пользователь не может арендовать свой же item");
        }
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Вещь недоступна");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ItemNotAvailableException("Недопустимая длительность аренды");
        }
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto,
                item,
                user,
                BookingState.WAITING));
        booking.setStatus(BookingState.WAITING);
        booking.setBooker(user);

        log.info("BookingService.addBooking: {} - Finished", booking);
        return booking;
    }

    @Override
    public Booking approveBooking(Long ownerId, Boolean approved, Long bookingId) {
        log.info("BookingService.approveBooking: {}, {} - Started", ownerId, bookingId);

        Booking booking = findById(bookingId);
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new UserNotFoundException("Пользователь не владелец вещи");
        }
        if (booking.getStatus() == BookingState.APPROVED && approved) {
            throw new ItemNotAvailableException("Одобрить дважды нельзя");
        }
        if (approved) {
            booking.setStatus(BookingState.APPROVED);
        } else {
            booking.setStatus(BookingState.REJECTED);
        }
        bookingRepository.save(booking);
        log.info("BookingService.approveBooking: {} - Finished", booking);
        return booking;
    }

    @Override
    public Booking getBookingById(Long bookerId, Long bookingId) {
        log.info("BookingService.getBookingById: {}, {} - Started", bookerId, bookingId);

        Booking booking = findById(bookingId);
        User user = userService.findById(bookerId);
        if (!booking.getBooker().equals(user) && !booking.getItem().getOwner().equals(user)) {
            throw new BookingNotFoundException("User не является автором бронирования, либо владельцем вещи");
        }
        log.info("BookingService.getBookingById: {} - Finished", booking);
        return booking;
    }

    public Booking findById(Long id) {
        log.info("BookingService.findById id : {}", id);
        return bookingRepository.findById(id)
                .orElseThrow(
                        () -> new BookingNotFoundException(
                                String.format("Бронирование с ID : %s не найдено", id))
                );
    }

    @Override
    public List<Booking> getAllBookingsByBooker(Long userId, BookingState state) {
        userService.findById(userId);

        List<Booking> bookings;
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case ALL: {
                bookings = bookingRepository.findByBooker_Id(userId, newestFirst);
                break;
            }
            case PAST: {
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), newestFirst);
                break;
            }
            case FUTURE: {
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), newestFirst);
                break;
            }
            case CURRENT: {
                bookings = bookingRepository.findCurrentForDate(userId, LocalDateTime.now(), newestFirst);
                break;
            }
            case WAITING: {
                bookings = bookingRepository.findPending(userId, newestFirst);
                break;
            }
            case REJECTED: {
                bookings = bookingRepository.findCanceled(userId, newestFirst);
                break;
            }
            case UNSUPPORTED_STATUS:
                throw new StateNotFoundException("Unknown state: UNSUPPORTED_STATUS");
            default:
                bookings = new ArrayList<>();
        }
        return bookings;
    }

    @Override
    public List<Booking> getAllBookingsByOwner(Long userId, BookingState state) {
        userService.findById(userId);

        List<Booking> bookings;
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case ALL: {
                bookings = bookingRepository.findByItem_Owner_Id(userId, newestFirst);
                break;
            }
            case PAST: {
                bookings = bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), newestFirst);
                break;
            }
            case FUTURE: {
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), newestFirst);
                break;
            }
            case CURRENT: {
                bookings = bookingRepository.findOwnerCurrentForDate(userId, LocalDateTime.now(), newestFirst);
                break;
            }
            case WAITING: {
                bookings = bookingRepository.findOwnerPending(userId, newestFirst);
                break;
            }
            case REJECTED: {
                bookings = bookingRepository.findOwnerCanceled(userId, newestFirst);
                break;
            }
            case UNSUPPORTED_STATUS:
                throw new StateNotFoundException("Unknown state: UNSUPPORTED_STATUS");
            default:
                bookings = new ArrayList<>();
        }
        return bookings;
    }
}
