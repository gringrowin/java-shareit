package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.stream.Collectors;

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
        Item item = itemService.findById(bookingDto.getItemId());
        User user = userService.findById(bookerId);

        if (item.getOwner().getId().equals(bookerId)) {
            throw new UserNotFoundException("Пользователь не может арендовать свой же item");
        }
        if (!item.getAvailable()) {
            log.error("item {} не доступен для бронирования", bookingDto.getItemId());
            throw new ItemNotAvailableException ("Вещь недоступна");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ItemNotAvailableException ("Недопустимая длительность аренды");
        }
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto,
                item,
                user,
                BookingState.WAITING));
        booking.setStatus(BookingState.WAITING);
        booking.setBooker(user);
        return booking;
    }

    @Override
    public Booking approveBooking(Long ownerId, Boolean approved, Long bookingId) {
        Booking booking = findById(bookingId);
        log.info("patch id владельца предмета: {}, id пользователя {}", booking.getBooker().getId(), ownerId);
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new UserNotFoundException ("Пользователь не владелец вещи");
        }
        if (booking.getStatus() == BookingState.APPROVED && approved) {
            throw new ItemNotAvailableException ("Одобрить дважды нельзя");
        }
        if (approved) {
            booking.setStatus(BookingState.APPROVED);
        } else {
            booking.setStatus(BookingState.REJECTED);
        }
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    public Booking getBookingById(Long bookerId, Long bookingId) {
        Booking booking = findById(bookingId);
        log.info("id владельца предмета: {}, id пользователя {}", booking.getBooker().getId(), bookerId);
        User user = userService.findById(bookerId);
        if (!booking.getBooker().equals(user) && !booking.getItem().getOwner().equals(user)) {
            throw new BookingNotFoundException("User не является автором бронирования, либо владельцем вещи");
        }
        return booking;
    }

    @Override
    public List<Booking> getAllBookingsByBookerAndState(Long bookerId, BookingState state) {
        return getAllBookingsByState(bookerId, state)
                .stream()
                .filter(booking -> booking.getBooker().getId().equals(bookerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> getAllBookingsByOwnerAndState(Long bookerId, BookingState state) {
        return getAllBookingsByState(bookerId, state)
                .stream()
                .filter(booking -> booking.getItem().getOwner().getId().equals(bookerId))
                .collect(Collectors.toList());
    }

    private List<Booking> getAllBookingsByState(Long bookerId, BookingState state) {
      userService.findById(bookerId);

        switch (state) {
            case ALL:
                return bookingRepository.findByStatusInOrderByStartDesc(List.of(BookingState.values()));
            case PAST:
                return bookingRepository.findByEndBeforeOrderByStartDesc(LocalDateTime.now());

            case CURRENT:
                return bookingRepository.findByStartBeforeAndEndAfterOrderByStartDesc(
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

            case FUTURE:
                return bookingRepository.findByStartAfterOrderByStartDesc(LocalDateTime.now());

            case UNSUPPORTED_STATUS:
                throw new StateNotFoundException("Unknown state: UNSUPPORTED_STATUS");

            default:
                return bookingRepository.findByStatusInOrderByStartDesc(List.of(state));
        }
    }
    public Booking findById(Long id) {
        log.info("ItemService.findById id : {}", id);
        return bookingRepository.findById(id)
                .orElseThrow(
                        () -> new BookingNotFoundException(
                                String.format("Бронирование с ID : %s не найдена", id))
                );
    }
}
