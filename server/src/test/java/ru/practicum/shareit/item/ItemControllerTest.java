package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private ItemDto itemDto;

    private ItemOutputDto itemOutputDto;

    @BeforeEach
    void setTestData() {
        itemDto = new ItemDto();
            itemDto.setId(1L);
            itemDto.setName("name");
            itemDto.setDescription("description");
            itemDto.setAvailable(true);
            itemDto.setRequestId(1L);

        itemOutputDto = new ItemOutputDto();
            itemOutputDto.setId(1L);
            itemOutputDto.setName("name");
            itemOutputDto.setDescription("description");
            itemOutputDto.setAvailable(true);
            itemOutputDto.setRequestId(1L);
    }

    @SneakyThrows
    @Test
    void getItemWhenInvokedWithInvalidUserIdThenReturned404NotFound() {
        when(itemService.getItem(1L, 1L)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());

        verify(itemService).getItem(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getItemWhenInvokedWithInvalidItemIdThenReturned404NotFound() {
        when(itemService.getItem(10L, 1L)).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", 10)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());

        verify(itemService).getItem(10L, 1L);
    }

    @SneakyThrows
    @Test
    void getItemWhenInvokedWithValidParamsThenReturnedItem() {
        when(itemService.getItem(1L, 1L)).thenReturn(itemOutputDto);

        String response = mockMvc.perform(get("/items/{itemId}", 1)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getItem(1L, 1L);
        assertEquals(objectMapper.writeValueAsString(itemOutputDto), response);
    }

    @SneakyThrows
    @Test
    void createWhenInvokedWithValidItemThenReturnedOkWithCreatedItem() {
        itemDto.setId(null);

        when(itemService.create(1L, itemDto)).thenReturn(itemOutputDto);

        String response = mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).create(1L, itemDto);
        assertEquals(objectMapper.writeValueAsString(itemOutputDto), response);
    }

    @SneakyThrows
    @Test
    void updateWhenInvokedWithValidItemThenReturnedOkWithCreatedItem() {

        when(itemService.update(1L, 1L, itemDto)).thenReturn(itemOutputDto);

        String response = mockMvc.perform(patch("/items/{itemId}", 1)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).update(1L, 1L, itemDto);
        assertEquals(objectMapper.writeValueAsString(itemOutputDto), response);
    }

    @SneakyThrows
    @Test
    void deleteItemThenReturnedOk() {
        doNothing().when(itemService).deleteItem(1L, 1L);

        String response = mockMvc.perform(delete("/items/{itemId}", 1)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).deleteItem(1L, 1L);
        assertEquals(objectMapper.writeValueAsString(itemDto.getId()), response);

    }

    @SneakyThrows
    @Test
    void getItemsByUserIdThenReturnedOk() {
        when(itemService.getItemsByUserId(1L)).thenReturn(List.of(itemOutputDto));

        String response = mockMvc.perform(get("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getItemsByUserId(1L);
        assertEquals(objectMapper.writeValueAsString(List.of(itemOutputDto)), response);
    }

    @SneakyThrows
    @Test
    void searchItemsThenReturnedOk() {
        when(itemService.searchItems("searchText")).thenReturn(List.of(itemOutputDto));

        String response = mockMvc.perform(get("/items/search")
                        .contentType("application/json")
                        .param("text", "searchText"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).searchItems("searchText");
        assertEquals(objectMapper.writeValueAsString(List.of(itemOutputDto)), response);
    }

    @SneakyThrows
    @Test
    void addCommentThenReturnedOk() {
        CommentDto commentDto = new CommentDto();

        when(itemService.addComment(1L, 1L, commentDto)).thenReturn(commentDto);

        String response = mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).addComment(1L, 1L, commentDto);
        assertEquals(objectMapper.writeValueAsString(commentDto), response);
    }
}