package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestOutputDtoTest {

    @Autowired
    private JacksonTester<ItemRequestOutputDto> jacksonTester;

    @SneakyThrows
    @Test
    void itemRequestOutputDtoToJsonTest() {
        ItemRequestOutputDto itemRequestOutputDto = new ItemRequestOutputDto();
                itemRequestOutputDto.setId(1L);
                itemRequestOutputDto.setDescription("description");
                itemRequestOutputDto.setCreated(LocalDateTime.now());
                itemRequestOutputDto.setItems(List.of(new ItemDto()));

        JsonContent<ItemRequestOutputDto> result = jacksonTester.write(itemRequestOutputDto);

        assertThat(result).hasJsonPathValue("$.id", itemRequestOutputDto.getId());
        assertThat(result).hasJsonPathValue("$.description", itemRequestOutputDto.getDescription());
        assertThat(result).hasJsonPathValue("$.created", itemRequestOutputDto.getCreated());
        assertThat(result).hasJsonPathValue("$.items", itemRequestOutputDto.getItems());
    }
}

