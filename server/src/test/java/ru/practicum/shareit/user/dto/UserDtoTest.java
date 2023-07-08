package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @SneakyThrows
    @Test
    void userDtoToJsonTest() {
        UserDto userDto = new UserDto(1L, "name","mail@mail.ru");

        JsonContent<UserDto> result = jacksonTester.write(userDto);

        assertThat(result).hasJsonPathValue("$.id", userDto.getId());
        assertThat(result).hasJsonPathValue("$.name", userDto.getName());
        assertThat(result).hasJsonPathValue("$.email", userDto.getEmail());
    }

    @SneakyThrows
    @Test
    void jsonToUserDtoTest() {
        UserDto userDto = new UserDto(1L, "name", "mail@mail.ru");

        UserDto userDtoFromJson = jacksonTester
                .parseObject("{\"id\":1,\"name\":\"name\",\"email\":\"mail@mail.ru\"}");

        assertThat(userDtoFromJson).isEqualTo(userDto);
    }
}