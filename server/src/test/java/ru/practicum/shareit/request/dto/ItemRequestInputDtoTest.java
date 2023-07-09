package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestInputDtoTest {
    @Autowired
    private JacksonTester<ItemRequestInputDto> json;

    @SneakyThrows
    @Test
    void itemRequestInputDtoToJson() {
        ItemRequestInputDto itemRequestInputDto = new ItemRequestInputDto();
            itemRequestInputDto.setDescription("description");

        ItemRequestInputDto requestInputDtoToJson =
                json.parseObject("{\"description\":\"description\"}");

        assertThat(requestInputDtoToJson).isEqualTo(itemRequestInputDto);
    }
}