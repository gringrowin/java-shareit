package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    UserDto userDto;
    User user;

    @BeforeEach
    void setTestData() {
        userDto = new UserDto();
            userDto.setId(1L);
            userDto.setName("name");
            userDto.setEmail("mail@mail.ru");

        user = new User();
            user.setId(1L);
            user.setName("name");
            user.setEmail("mail@mail.ru");
    }

    @Test
    void toUserDtoTest() {
        UserDto userDtoTest = UserMapper.toUserDto(user);
        assertEquals(userDtoTest, userDto);
    }

    @Test
    void toUserWithOneArgumentTest() {
        User userTest = UserMapper.toUser(userDto);
        assertEquals(userTest, user);
    }

    @Test
    void toUserWithTwoArgumentsTestWhenFieldsEquals() {
        User userTest = UserMapper.toUser(user, userDto);
        assertEquals(userTest, user);
    }

    @Test
    void toUserWithTwoArgumentsTestWhenFieldsNotEquals() {
        User anotherUser = new User();
            anotherUser.setName("anotherName");
            anotherUser.setEmail("anotherMail@mail.ru");
        User userTest = UserMapper.toUser(anotherUser, userDto);
        assertEquals(userTest.getName(), user.getName());
        assertEquals(userTest.getEmail(), user.getEmail());
    }

    @Test
    void toUserWithTwoArgumentsTestWhenUserDtoNameIsNull() {
        userDto.setName(null);
        User userTest = UserMapper.toUser(user, userDto);
        assertEquals(userTest, user);
    }

    @Test
    void toUserWithTwoArgumentsTestWhenUserDtoEmailIsNull() {
        userDto.setEmail(null);
        User userTest = UserMapper.toUser(user, userDto);
        assertEquals(userTest, user);
    }

    @Test
    void toUserWithTwoArgumentsTestWhenUserDtoEmailAnother() {
        userDto.setEmail("anotherMail@mail.ru");
        User userTest = UserMapper.toUser(user, userDto);
        assertEquals(userTest, user);
    }
}