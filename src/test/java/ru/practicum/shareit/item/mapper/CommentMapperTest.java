package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.DetailedCommentDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentMapperTest {

    public static final long ID = 1L;
    public static final LocalDateTime CREATED_DATE = LocalDateTime.now();

    private Item item;
    private User user;
    private Comment comment;
    private CreateCommentDto createCommentDto;

    @BeforeEach
    public void beforeEach() {
        item = new Item(
                ID,
                "name",
                "description",
                true,
                ID,
                ID + 1);
        user = new User(ID, "name", "user@emali.com");
        comment = new Comment(ID, "comment", item, user, CREATED_DATE);
        createCommentDto = new CreateCommentDto("comment");
    }


    @Test
    public void toModelTest() {
        Comment result = CommentMapper.toModel(createCommentDto, item, user);

        assertNotNull(result);
        assertEquals(createCommentDto.getText(), result.getText());
        assertEquals(user.getId(), result.getAuthor().getId());
        assertEquals(item.getId(), result.getItem().getId());
    }

    @Test
    public void toCommentDetailedDtoTest() {
        DetailedCommentDto result = CommentMapper.toCommentDetailedDto(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getAuthor().getName(), result.getAuthorName());
        assertEquals(comment.getText(), result.getText());
    }

    @Test
    public void toCommentDetailedDtoListTest() {
        List<DetailedCommentDto> result = CommentMapper
                .toCommentDetailedDtoList(Collections.singletonList(comment));

        assertNotNull(result);
        assertEquals(result.get(0).getId(), comment.getId());
        assertEquals(result.get(0).getText(), comment.getText());
        assertEquals(result.get(0).getAuthorName(), comment.getAuthor().getName());
    }
}