package ru.practicum.shareit.booking.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.StateNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingRepository bookingRepository;

    private BookingService bookingService;

    private Booking booking;
    private BookingDto bookingDto;
    private Item item;
    private User user;

    @BeforeEach
    void setBookingServiceTestData() {
        bookingService = new BookingServiceImpl(bookingRepository, itemService, userService);

        user = new User();
            user.setId(1L);

        item = new Item();
            item.setId(1L);
            item.setOwner(user);
            item.setAvailable(true);

        LocalDateTime start = LocalDateTime.of(2023, 7, 2,21, 30);
        LocalDateTime end = LocalDateTime.of(2023, 7, 2,21, 35);

        bookingDto = new BookingDto();
            bookingDto.setStart(start);
            bookingDto.setEnd(end);
            bookingDto.setItemId(1L);

        booking = new Booking();
            booking.setId(1L);
            booking.setStart(start);
            booking.setEnd(end);
            booking.setBooker(user);
            booking.setItem(item);
            booking.setStatus(BookingState.ALL);
   }

    @Test
    void addBookingWhenValidParams() {
        User owner = new User();
            owner.setId(2L);
        item.setOwner(owner);

        when(itemService.findById(1L)).thenReturn(item);
        when(userService.findById(1L)).thenReturn(user);
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking bookingTest = bookingService.addBooking(1L, bookingDto);

        verify(itemService).findById(1L);
        verify(userService).findById(1L);
        verify(bookingRepository).save(any());

        assertEquals(bookingTest, booking);
    }

    @Test
    void addBookingWhenInvalidOwner() {

        when(itemService.findById(1L)).thenReturn(item);
        when(userService.findById(1L)).thenReturn(user);
        assertThrows(UserNotFoundException.class, () ->  bookingService.addBooking(1L, bookingDto));
    }

    @Test
    void addBookingWhenInvalidItemAvailable() {
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);

        item.setAvailable(false);

        when(itemService.findById(1L)).thenReturn(item);
        when(userService.findById(1L)).thenReturn(user);
        assertThrows(ItemNotAvailableException.class,
                () ->  bookingService.addBooking(1L, bookingDto));
    }

    @Test
    void addBookingWhenStarTimeEqualsEndTime() {
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);
        bookingDto.setStart(bookingDto.getEnd());

        when(itemService.findById(1L)).thenReturn(item);
        when(userService.findById(1L)).thenReturn(user);
        assertThrows(ItemNotAvailableException.class,
                () ->  bookingService.addBooking(1L, bookingDto));
    }

    @Test
    void addBookingWhenStarTimeBeforeEndTime() {
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);
        bookingDto.setEnd(bookingDto.getStart().minusMinutes(10));

        when(itemService.findById(1L)).thenReturn(item);
        when(userService.findById(1L)).thenReturn(user);
        assertThrows(ItemNotAvailableException.class,
                () ->  bookingService.addBooking(1L, bookingDto));
    }

    @Test
    void approveBookingWhenValidParamsAndApprovedIsTrue() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking bookingTest = bookingService.approveBooking(1L, true, 1L);

        verify(bookingRepository, times(1)).findById(1L);

        assertEquals(bookingTest.getStatus(), BookingState.APPROVED);
    }

    @Test
    void approveBookingWhenValidParamsAndApprovedIsFalse() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking bookingTest = bookingService.approveBooking(1L, false, 1L);

        verify(bookingRepository, times(1)).findById(1L);

        assertEquals(bookingTest.getStatus(), BookingState.REJECTED);
    }

    @Test
    void approveBookingWhenInvalidOwner() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(UserNotFoundException.class,
                () -> bookingService.approveBooking(99L, true, 1L));
    }

    @Test
    void approveBookingWhenBookingStatusIsApproved() {
        booking.setStatus(BookingState.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ItemNotAvailableException.class,
                () -> bookingService.approveBooking(1L, true, 1L));
    }

    @Test
    void getBookingByIdWhenValidParams() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userService.findById(1L)).thenReturn(user);

        Booking bookingTest = bookingService.getBookingById(1L, 1L);

        verify(bookingRepository, times(1)).findById(1L);
        verify(userService, times(1)).findById(1L);
        assertEquals(booking, bookingTest);
    }

    @Test
    void getBookingByIdWhenUserNotEqualsBookerAndItemOwner() {
        User testUser = new User();
        testUser.setId(2L);
        booking.setBooker(testUser);
        item.setOwner(testUser);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userService.findById(1L)).thenReturn(user);

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(1L, 1L));
    }

    @Test
    void getBookingByIdWhenInvalidUserId() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(1L, 1L));
    }

    @Test
    void getAllBookingsByBookerWhenDifferentStatusTest() {

        when(userService.findById(1L)).thenReturn(user);

        bookingService.getAllBookingsByBooker(1L, BookingState.ALL, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByBookerIdOrderByStartDesc(
                        anyLong(),
                        any(PageRequest.class));

        bookingService.getAllBookingsByBooker(1L, BookingState.PAST, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(),
                        any(LocalDateTime.class),
                        any(PageRequest.class));

        bookingService.getAllBookingsByBooker(1L, BookingState.FUTURE, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartAfterOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(PageRequest.class));

        bookingService.getAllBookingsByBooker(1L, BookingState.CURRENT, 1, 1);
        verify(bookingRepository, times(1))
                .findCurrentForDate(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(PageRequest.class));

        bookingService.getAllBookingsByBooker(1L, BookingState.WAITING, 1, 1);
        verify(bookingRepository, times(1))
                .findPending(
                        anyLong(),
                        any(PageRequest.class));

        bookingService.getAllBookingsByBooker(1L, BookingState.REJECTED, 1, 1);
        verify(bookingRepository, times(1))
                .findCanceled(
                        anyLong(),
                        any(PageRequest.class));
    }

    @Test
    void getAllBookingsByBookerWhenUnsupportedStatus() {
        when(userService.findById(1L)).thenReturn(user);

        assertThrows(StateNotFoundException.class,
                () -> bookingService.getAllBookingsByBooker(
                        1L, BookingState.UNSUPPORTED_STATUS, 1, 1));
    }

    @Test
    void getAllBookingsByOwnerWhenDifferentStatusTest() {

        when(userService.findById(1L)).thenReturn(user);

        bookingService.getAllBookingsByOwner(1L, BookingState.ALL, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdOrderByStartDesc(
                        anyLong(),
                        any(PageRequest.class));

        bookingService.getAllBookingsByOwner(1L, BookingState.PAST, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(),
                        any(LocalDateTime.class),
                        any(PageRequest.class));

        bookingService.getAllBookingsByOwner(1L, BookingState.FUTURE, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(PageRequest.class));

        bookingService.getAllBookingsByOwner(1L, BookingState.CURRENT, 1, 1);
        verify(bookingRepository, times(1))
                .findOwnerCurrentForDate(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(PageRequest.class));

        bookingService.getAllBookingsByOwner(1L, BookingState.WAITING, 1, 1);
        verify(bookingRepository, times(1))
                .findOwnerPending(
                        anyLong(),
                        any(PageRequest.class));

        bookingService.getAllBookingsByOwner(1L, BookingState.REJECTED, 1, 1);
        verify(bookingRepository, times(1))
                .findOwnerCanceled(
                        anyLong(),
                        any(PageRequest.class));
    }

    @Test
    void getAllBookingsByOwnerWhenUnsupportedStatus() {
        when(userService.findById(1L)).thenReturn(user);

        assertThrows(StateNotFoundException.class,
                () -> bookingService.getAllBookingsByOwner(
                        1L, BookingState.UNSUPPORTED_STATUS, 1, 1));
    }
}