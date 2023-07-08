package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private UserDto testUserDto() {
        return new UserDto(
                1L, "name", "mail@mail.com");
    }

    @SneakyThrows
    @Test
    void getUserWhenInvokedWithInvalidUserIdThenReturned404NotFound() {
        when(userService.getUser(1L)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/users/{userId}", 1))
                .andExpect(status()
                        .isNotFound());

        verify(userService).getUser(1L);
    }

    @SneakyThrows
    @Test
    void getUserWhenInvokedWithValidIdUserThenReturnedWithUserDto() {
        UserDto userToGet = testUserDto();
        Long id = 1L;
        when(userService.getUser(id)).thenReturn(userToGet);

        String response = mockMvc.perform(get("/users/{userId}", id))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).getUser(id);
        assertEquals(objectMapper.writeValueAsString(userToGet), response);
    }

    @SneakyThrows
    @Test
    void createWhenInvokedWithValidUserThenReturnedOkWithCreatedUser() {
        UserDto userToCreate = testUserDto();
        userToCreate.setId(null);

        when(userService.create(userToCreate)).thenReturn(userToCreate);

        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).create(userToCreate);
        assertEquals(objectMapper.writeValueAsString(userToCreate), response);
    }

    @SneakyThrows
    @Test
    void createWhenInvokedWithInvalidUserThenReturned4xxClientError() {
        UserDto userToCreate = new UserDto();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().is4xxClientError());

        verify(userService, never()).create(userToCreate);
    }

    @SneakyThrows
    @Test
    void createWhenInvokedWithInvalidUserEmailThenReturned4xxClientError() {
        UserDto userToCreate = testUserDto();
        userToCreate.setEmail("mail");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().is4xxClientError());

        verify(userService, never()).create(userToCreate);
    }

    @SneakyThrows
    @Test
    void updateWhenInvokedWithInvalidUserEmailThenReturned4xxClientError() {
        UserDto userToUpdate = testUserDto();
        userToUpdate.setEmail("mail");

        mockMvc.perform(patch("/users/{userId}", userToUpdate.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().is4xxClientError());

        verify(userService, never()).update(userToUpdate.getId(), userToUpdate);
    }

    @SneakyThrows
    @Test
    void updateWhenInvokedWithValidUserThenReturnedOkWithUpdatedUser() {
        UserDto userToUpdate = testUserDto();

        when(userService.update(userToUpdate.getId(), userToUpdate)).thenReturn(userToUpdate);

        String response = mockMvc.perform(patch("/users/{userId}", userToUpdate.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).update(userToUpdate.getId(), userToUpdate);
        assertEquals(objectMapper.writeValueAsString(userToUpdate), response);
    }

    @SneakyThrows
    @Test
    void getAllWhenEmptyStorageThenReturnedOkWithEmptyList() {
        List<UserDto> allUsers = Collections.emptyList();
        when(userService.getAll()).thenReturn(allUsers);

        String response = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).getAll();
        assertEquals(objectMapper.writeValueAsString(allUsers), response);
    }

    @SneakyThrows
    @Test
    void deleteUserThenReturnedOk() {
        UserDto userDto = testUserDto();
        doNothing().when(userService).deleteUser(userDto.getId());

        String response = mockMvc.perform(delete("/users/{userId}", userDto.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).deleteUser(userDto.getId());
        assertEquals(objectMapper.writeValueAsString(userDto.getId()), response);
    }
}