package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidCommentException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.mapper.CommentMapper;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.comments.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;

    private ItemService itemService;

    private final Item item = mock(Item.class);
    private final ItemDto itemDto = mock(ItemDto.class);
    private final User user = mock(User.class);
    private final ItemOutputDto itemOutputDto = mock(ItemOutputDto.class);
    private final CommentDto commentDto = mock(CommentDto.class);
    private final Comment comment = mock(Comment.class);
    private final Booking booking = mock(Booking.class);
    private final ItemRequest itemRequest = mock((ItemRequest.class));

    private LocalDateTime dateTime;

    private MockedStatic<ItemMapper> itemMapperMockedStatic;
    private MockedStatic<CommentMapper> commentMapperMockedStatic;

    private MockedStatic<LocalDateTime> localDateTimeMockedStatic;

    @BeforeEach
    void setItemService() {
        itemService = new ItemServiceImpl(
                itemRepository,
                bookingRepository,
                commentRepository,
                userService,
                itemRequestRepository
                );

        itemMapperMockedStatic = Mockito.mockStatic(ItemMapper.class);
        when(ItemMapper.toItem(any(), any(), any())).thenReturn(item);
        when(ItemMapper.toItemDto(any())).thenReturn(itemDto);
        when(ItemMapper.toItemOutputDto(any(), any(), any(), any(), any()))
                .thenReturn(itemOutputDto);

        commentMapperMockedStatic = Mockito.mockStatic(CommentMapper.class);
        when(CommentMapper.toComment(any(), any(), any())).thenReturn(comment);
        when(CommentMapper.toCommentDto(any())).thenReturn(commentDto);

        Clock clock = Clock.fixed(Instant.parse("2014-12-22T10:15:30.00Z"), ZoneId.of("UTC"));
        dateTime = LocalDateTime.now(clock);
        localDateTimeMockedStatic = mockStatic(LocalDateTime.class);
        when(LocalDateTime.now()).thenReturn(dateTime);
    }

    @AfterEach
    void closeItemService() {
        itemMapperMockedStatic.close();
        commentMapperMockedStatic.close();
        localDateTimeMockedStatic.close();
    }

    @Test
    void getItemTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemOutputDto itemOutputDtoTest = itemService.getItem(1L, 1L);

        verify(itemRepository, times(1)).findById(1L);

        assertEquals(itemOutputDtoTest, itemOutputDto);
    }

    @Test
    void createTest() {
        when(userService.findById(1L)).thenReturn(user);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));

        ItemOutputDto itemOutputDtoTest = itemService.create(1L, itemDto);

        verify(itemRepository, times(1)).save(item);
        verify(userService).findById(1L);

        assertEquals(itemOutputDtoTest, itemOutputDto);
    }

    @Test
    void updateTestWhenValidOwner() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.findById(1L)).thenReturn(user);
        when(item.getOwner()).thenReturn(user);
        when(itemRepository.save(item)).thenReturn(item);

        ItemOutputDto itemOutputDtoTest = itemService.update(1L, 1L, itemDto);

        verify(itemRepository).findById(1L);
        verify(userService).findById(1L);
        verify(item).getOwner();
        verify(itemRepository).save(item);

        assertEquals(itemOutputDtoTest, itemOutputDto);
    }

    @Test

    void updateTestWhenItemDtoWithNameAndDescriptionAbdAvailable() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.findById(1L)).thenReturn(user);
        when(item.getOwner()).thenReturn(user);
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto itemDtoTest = new ItemDto();
        itemDtoTest.setName("name");
        itemDtoTest.setDescription("description");
        itemDtoTest.setAvailable(true);

        ItemOutputDto itemOutputDtoTest = itemService.update(1L, 1L, itemDtoTest);

        verify(itemRepository).findById(1L);
        verify(userService).findById(1L);
        verify(item).getOwner();
        verify(itemRepository).save(item);

        assertEquals(itemOutputDtoTest, itemOutputDto);
    }

    @Test
    void updateTestWhenInValidOwner() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.findById(1L)).thenReturn(user);
        when(item.getOwner()).thenReturn(new User());

        verify(itemRepository, never()).save(item);

        assertThrows(ItemNotFoundException.class, () -> itemService.update(1L, 1L, itemDto));
    }

    @Test
    void deleteItemTestWhenValidOwner() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(item.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        doNothing().when(itemRepository).delete(item);
        itemService.deleteItem(1L, 1L);
        verify(itemRepository, times(1)).delete(item);
    }

    @Test
    void deleteItemTestWhenInvalidOwner() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(item.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(2L);

        itemService.deleteItem(1L, 1L);

        verify(itemRepository, never()).delete(item);
    }

    @Test
    void getItemsByUserIdTest() {
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));

        List<ItemOutputDto> itemOutputDtoTest = itemService.getItemsByUserId(1L);

        verify(itemRepository, times(1)).findByOwnerId(1L);

        assertEquals(itemOutputDtoTest, List.of(itemOutputDto));
    }

    @Test
    void searchItemsTest() {
        when(itemRepository.search("text")).thenReturn(List.of(item));
        when(item.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(item.getId()).thenReturn(1L);

        List<ItemOutputDto> itemOutputDtoTest = itemService.searchItems("text");

        verify(itemRepository, times(1)).search("text");

        assertEquals(itemOutputDtoTest, List.of(itemOutputDto));
    }

    @Test
    void searchItemsWhenTestIsBlankOrNull() {

        List<ItemOutputDto> itemOutputDtoTest1 = itemService.searchItems("");
        List<ItemOutputDto> itemOutputDtoTest2 = itemService.searchItems(null);

        assertTrue(itemOutputDtoTest1.isEmpty());
        assertTrue(itemOutputDtoTest2.isEmpty());
    }

    @Test
    void findByIdTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item itemTest = itemService.findById(1L);

        verify(itemRepository, times(1)).findById(1L);

        assertEquals(itemTest, item);
    }

    @Test
    void findByIdWhenInvalidItemIdThenThrowItemNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findById(1L));
    }

    @Test
    void addCommentWhenAllValid() {

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.findById(1L)).thenReturn(user);
        when(bookingRepository
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        1L,
                      dateTime))
                .thenReturn(List.of(booking));
        when(commentDto.getText()).thenReturn("text");

        CommentDto commentDtoTest = itemService.addComment(1L, 1L, commentDto);

        verify(itemRepository).findById(1L);
        verify(userService).findById(1L);
        verify(bookingRepository)
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        1L, dateTime);

        assertEquals(commentDtoTest, commentDto);
    }

    @Test
    void addCommentWhenBookingListIsEmpty() {

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.findById(1L)).thenReturn(user);
        when(bookingRepository
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        1L,
                        dateTime))
                .thenReturn(Collections.emptyList());
        when(commentDto.getText()).thenReturn("text");

        assertThrows(ItemNotAvailableException.class,
                () -> itemService.addComment(1L, 1L, commentDto));
    }

    @Test
    void addCommentWhenCommentIsEmpty() {

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.findById(1L)).thenReturn(user);
        when(bookingRepository
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        1L,
                        dateTime))
                .thenReturn(List.of(booking));
        when(commentDto.getText()).thenReturn("");

        assertThrows(InvalidCommentException.class,
                () -> itemService.addComment(1L, 1L, commentDto));
    }

    @Test
    void getItemsByRequestId() {
        when(itemRepository.findItemsByItemRequestId(1L)).thenReturn(List.of(item));

        List<ItemDto> itemDtoList = itemService.getItemsByRequestId(1L);

        verify(itemRepository, times(1)).findItemsByItemRequestId(1L);

        assertEquals(itemDtoList, List.of(itemDto));
    }
}