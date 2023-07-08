package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql({"/schema.sql", "/test_data.sql"})
@Import({UserServiceImpl.class})
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void getUser() {
        UserDto userDto1 = userService.getUser(1L);
        assertEquals(userDto1.getId(), 1);
        assertEquals(userDto1.getName(), "user1");
        assertEquals(userDto1.getEmail(), "user1@mail.ru");

        UserDto userDto4 = userService.getUser(4L);
        assertEquals(userDto4.getId(), 4);
        assertEquals(userDto4.getName(), "user4");
        assertEquals(userDto4.getEmail(), "user4@mail.ru");
    }
}