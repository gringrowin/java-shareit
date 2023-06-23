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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
}