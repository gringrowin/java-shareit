package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql({"/schema.sql", "/test_data.sql"})
@Import({BookingServiceImpl.class})
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Test
    void getBookingById() {
        Booking booking = bookingService.getBookingById(4L, 1L);
        assertEquals(booking.getId(), 1L);
        assertEquals(booking.getItem().getId(), 1L);
        assertEquals(booking.getBooker().getId(), 4L);
        assertEquals(booking.getStatus(), BookingState.APPROVED);
        assertEquals(booking.getStart(),
                LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        assertEquals(booking.getEnd(),
                LocalDateTime.of(2021, 1, 1, 0, 0, 0));
    }
}