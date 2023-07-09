package ru.practicum.shareit.item.comments.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    CommentDto commentDto;

    Comment comment;

    Item item;

    User user;

    @BeforeEach
    void setTestData() {
        commentDto = new CommentDto();
            commentDto.setId(1L);
            commentDto.setText("text");
            commentDto.setAuthorName("name");
            commentDto.setItemId(1L);
            commentDto.setCreated(LocalDateTime.of(2023, 7, 5, 22, 15));

        user = new User();
            user.setName("name");
        item = new Item();
            item.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2023, 7, 5, 22, 15));
    }

    @Test
    void toCommentDtoTest() {
        CommentDto commentDtoTest = CommentMapper.toCommentDto(comment);
        assertEquals(commentDtoTest, commentDto);
    }

    @Test
    void toCommentWhenCommentDtoNotNull() {
        Comment commentTest = CommentMapper.toComment(commentDto, user, item);
        assertEquals(commentTest.getId(), comment.getId());
        assertEquals(commentTest.getText(), comment.getText());
        assertEquals(commentTest.getItem(), comment.getItem());
        assertEquals(commentTest.getAuthor(), comment.getAuthor());

    }

    @Test
    void toCommentWhenCommentDtoNull() {
        Comment commentTest = CommentMapper.toComment(null, user, item);
        assertNull(commentTest);
    }
}