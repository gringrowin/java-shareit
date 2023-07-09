package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private ItemRequestInputDto itemRequestInputDto;

    private ItemRequestOutputDto itemRequestOutputDto;

    @BeforeEach
    void setTestData() {
        itemRequestInputDto = new ItemRequestInputDto();
            itemRequestInputDto.setDescription("description");
        itemRequestOutputDto = new ItemRequestOutputDto();
            itemRequestOutputDto.setId(1L);
            itemRequestOutputDto.setDescription("description");
            itemRequestOutputDto.setItems(Collections.emptyList());
    }

    @SneakyThrows
    @Test
    void createWhenInvokedWithValidRequestThenReturnedOkWithCreatedRequest() {

        when(itemRequestService.create(1L, itemRequestInputDto)).thenReturn(itemRequestOutputDto);

        String response = mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemRequestInputDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).create(1L, itemRequestInputDto);
        assertEquals(objectMapper.writeValueAsString(itemRequestOutputDto), response);
    }

    @SneakyThrows
    @Test
    void getItemRequestsInvokedWithInvalidUserIdThenReturned404NotFound() {

        when(itemRequestService.getItemRequests(1L)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/requests")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());

        verify(itemRequestService).getItemRequests(1L);
    }

    @SneakyThrows
    @Test
    void getItemRequestsInvokedWithValidUserIdThenOkAndReturnedListOfRequests() {

        when(itemRequestService.getItemRequests(1L)).thenReturn(List.of(itemRequestOutputDto));

        String response = mockMvc.perform(get("/requests")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getItemRequests(1L);
        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestOutputDto)), response);
    }



    @SneakyThrows
    @Test
    void getItemRequestsByOtherUsersInvokedWithInValidUserIdAndValidParamsThenReturned404NotFound() {
        Integer from = 0;
        Integer size = 1;
        when(itemRequestService.getItemRequestsByOtherUsers(20L, from, size))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/requests/all")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "20")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isNotFound());

        verify(itemRequestService).getItemRequestsByOtherUsers(20L, from, size);
    }

    @SneakyThrows
    @Test
    void getItemRequestsByOtherUsersInvokedWithValidUserIdAndValidParamsThenOkAndReturnedListOfRequests() {
        Integer from = 0;
        Integer size = 1;
        when(itemRequestService.getItemRequestsByOtherUsers(1L, from, size))
                .thenReturn(List.of(itemRequestOutputDto));

        String response = mockMvc.perform(get("/requests/all")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getItemRequestsByOtherUsers(1L, from, size);
        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestOutputDto)), response);
    }

    @SneakyThrows
    @Test
    void getItemRequestsByOtherUsersInvokedWithValidUserIdAndWithoutParamsThenReturnedListOfRequests() {
        Integer from = 0;
        Integer size = 20;
        when(itemRequestService.getItemRequestsByOtherUsers(1L, from, size))
                .thenReturn(List.of(itemRequestOutputDto));

        String response = mockMvc.perform(get("/requests/all")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getItemRequestsByOtherUsers(1L, from, size);
        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestOutputDto)), response);
    }

    @SneakyThrows
    @Test
    void getItemRequestsInvokedWithInvalidUserIdAndValidRequestIdThenReturned404NotFound() {
        when(itemRequestService.getItemRequestById(1L, 1L))
                .thenThrow(UserNotFoundException.class);


        mockMvc.perform(get("/requests/{itemRequestId}", 1L)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());

        verify(itemRequestService).getItemRequestById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getItemRequestsInvokedWithValidUserIdAndInvalidRequestIdThenReturned404NotFound() {
        when(itemRequestService.getItemRequestById(1L, 1L))
                .thenThrow(ItemRequestNotFoundException.class);

        mockMvc.perform(get("/requests/{itemRequestId}", 1L)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());

        verify(itemRequestService).getItemRequestById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getItemRequestsInvokedWithValidUserIdAndValidRequestIdThenOkAndReturnedRequest() {

        when(itemRequestService.getItemRequestById(1L, 1L))
                .thenReturn(itemRequestOutputDto);

        String response = mockMvc.perform(get("/requests/{itemRequestId}", 1L)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getItemRequestById(1L, 1L);
        assertEquals(objectMapper.writeValueAsString(itemRequestOutputDto), response);
    }
}