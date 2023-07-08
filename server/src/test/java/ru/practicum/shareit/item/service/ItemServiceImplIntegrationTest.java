package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql({"/schema.sql", "/test_data.sql"})
@Import({ItemServiceImpl.class})
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Test
    void getItem() {
        ItemOutputDto itemOutputDto1 = itemService.getItem(1L, 1L);
        assertEquals(itemOutputDto1.getId(), 1L);
        assertEquals(itemOutputDto1.getName(), "item1");
        assertEquals(itemOutputDto1.getDescription(), "description1");
        assertEquals(itemOutputDto1.getAvailable(), true);
        assertEquals(itemOutputDto1.getComments().size(), 2);
        assertNull(itemOutputDto1.getRequestId());

        ItemOutputDto itemOutputDto4 = itemService.getItem(4L, 1L);
        assertEquals(itemOutputDto4.getId(), 4L);
        assertEquals(itemOutputDto4.getName(), "item4");
        assertEquals(itemOutputDto4.getDescription(), "description4");
        assertEquals(itemOutputDto4.getAvailable(), true);
        assertEquals(itemOutputDto4.getComments().size(), 1);
        assertEquals(itemOutputDto4.getRequestId(), 1L);
    }
}