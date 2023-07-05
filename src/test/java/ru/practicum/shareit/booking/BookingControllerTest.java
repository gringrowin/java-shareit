package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private BookingDto bookingDto;

    private Booking booking;

    @BeforeEach
    void setTestData() {
        bookingDto = new BookingDto();
            bookingDto.setItemId(1L);
            bookingDto.setStart(LocalDateTime.MAX);
            bookingDto.setEnd(LocalDateTime.MAX);

        booking = new Booking();
            booking.setId(1L);
    }

    @SneakyThrows
    @Test
    void addBookingInvokedWithValidBookingThenReturnedBooking() {

        when(bookingService.addBooking(1L, bookingDto)).thenReturn(booking);

        String response = mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).addBooking(1L, bookingDto);
        assertEquals(objectMapper.writeValueAsString(booking), response);
    }

    @SneakyThrows
    @Test
    void addBookingInvokedWithInvalidBookingThenReturned4xxClientError() {
        bookingDto.setStart(LocalDateTime.MIN);
        bookingDto.setEnd(null);

        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).addBooking(1L, bookingDto);
    }

    @SneakyThrows
    @Test
    void approveBookingThenOkAndReturnedBooking() {
        when(bookingService.approveBooking(1L, true, 1L))
                .thenReturn(booking);

        String response = mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).approveBooking(1L, true, 1L);
        assertEquals(objectMapper.writeValueAsString(booking), response);
    }

    @SneakyThrows
    @Test
    void getBookingByIdThenOkAndReturnedBooking() {
        when(bookingService.getBookingById(1L, 1L))
                .thenReturn(booking);

        String response = mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getBookingById(1L, 1L);
        assertEquals(objectMapper.writeValueAsString(booking), response);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByBookerInvokedWithValidParamsThenReturnedListOfBooking() {
        Integer from = 0;
        Integer size = 1;
        when(bookingService.getAllBookingsByBooker(1L, BookingState.ALL, from, size))
                .thenReturn(List.of(booking));

        String response = mockMvc.perform(get("/bookings")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getAllBookingsByBooker(1L, BookingState.ALL, from, size);
        assertEquals(objectMapper.writeValueAsString(List.of(booking)), response);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByOwnerInvokedWithValidParamsThenReturnedListOfBooking() {
        Integer from = 0;
        Integer size = 1;
        when(bookingService.getAllBookingsByOwner(1L, BookingState.ALL, from, size))
                .thenReturn(List.of(booking));

        String response = mockMvc.perform(get("/bookings/owner")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getAllBookingsByOwner(1L, BookingState.ALL, from, size);
        assertEquals(objectMapper.writeValueAsString(List.of(booking)), response);
    }
}