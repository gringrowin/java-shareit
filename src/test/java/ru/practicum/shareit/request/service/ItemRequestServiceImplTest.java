package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    private ItemRequestService itemRequestService;

    private final ItemRequestInputDto itemRequestInputDto = mock(ItemRequestInputDto.class);

    private final ItemRequestOutputDto itemRequestOutputDto = mock(ItemRequestOutputDto.class);

    private final ItemRequest itemRequest = mock(ItemRequest.class);

    private final ItemDto itemDto = mock(ItemDto.class);

    private final User user = mock(User.class);

    private MockedStatic<ItemRequestMapper> itemRequestMapperMockedStatic;

    @BeforeEach
    void setItemRequestService() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemService, userService);

        itemRequestMapperMockedStatic = Mockito.mockStatic(ItemRequestMapper.class);
        when(ItemRequestMapper.toItemRequest(itemRequestInputDto, user)).thenReturn(itemRequest);
        when(ItemRequestMapper.toItemRequestOutputDto(itemRequest, List.of(itemDto)))
                .thenReturn(itemRequestOutputDto);
    }

    @AfterEach
    void closeItemRequestService() {
        itemRequestMapperMockedStatic.close();
    }

    @Test
    void getItemRequestByIdTest() {
        when(userService.findById(10L)).thenReturn(user);
        when(itemRequestRepository.findById(10L)).thenReturn(Optional.of(itemRequest));
        when(itemService.getItemsByRequestId(10L)).thenReturn(List.of(itemDto));

        ItemRequestOutputDto itemRequestOutputDtoTest =
                itemRequestService.getItemRequestById(10L, 10L);

        verify(userService, times(1)).findById(10L);
        verify(itemRequestRepository, times(1)).findById(10L);
        verify(itemService, times(1)).getItemsByRequestId(10L);

        assertEquals(itemRequestOutputDtoTest, itemRequestOutputDto);
    }

    @Test
    void getItemRequestsByOtherUsersTest() {
        Integer from = 1;
        Integer size = 1;
        when(userService.findById(10L)).thenReturn(user);
        when(itemRequestRepository
                .findByRequesterNotInOrderByCreatedDesc(
                        List.of(user),
                        PageRequest.of(from / size, size)))
                .thenReturn(List.of(itemRequest));
        when(itemService.getItemsByRequestId(itemRequest.getId())).thenReturn(List.of(itemDto));

        List<ItemRequestOutputDto> itemRequestOutputDtos =
                itemRequestService.getItemRequestsByOtherUsers(10L, from, size);

        verify(userService, times(1)).findById(10L);
        verify(itemRequestRepository, times(1))
                .findByRequesterNotInOrderByCreatedDesc(
                        List.of(user),
                        PageRequest.of(from / size, size));
        verify(itemService, times(1)).getItemsByRequestId(itemRequest.getId());

        assertEquals(itemRequestOutputDtos, List.of(itemRequestOutputDto));
    }

    @Test
    void getItemRequestsTests() {
        when(userService.findById(10L)).thenReturn(user);
        when(itemRequestRepository
                .findByRequesterOrderByCreatedDesc(user))
                .thenReturn(List.of(itemRequest));
        when(itemService.getItemsByRequestId(itemRequest.getId())).thenReturn(List.of(itemDto));

        List<ItemRequestOutputDto> itemRequestOutputDtos =
                itemRequestService.getItemRequests(10L);

        verify(userService, times(1)).findById(10L);
        verify(itemRequestRepository, times(1))
                .findByRequesterOrderByCreatedDesc(user);
        verify(itemService, times(1)).getItemsByRequestId(itemRequest.getId());

        assertEquals(itemRequestOutputDtos, List.of(itemRequestOutputDto));
    }

    @Test
    void createTest() {
        when(userService.findById(10L)).thenReturn(user);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemService.getItemsByRequestId(itemRequest.getId())).thenReturn(List.of(itemDto));

        ItemRequestOutputDto itemRequestOutputDto1 = itemRequestService.create(10L, itemRequestInputDto);

        verify(userService, times(1)).findById(10L);
        verify(itemRequestRepository, times(1)).save(itemRequest);
        verify(itemService, times(1)).getItemsByRequestId(itemRequest.getId());

        assertEquals(itemRequestOutputDto1, itemRequestOutputDto);
    }

    @Test
    void findByIdInvalidItemRequestIdThenThrowItemRequestNotFoundException() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.findById(10L));
    }

    @Test
    void findByIdValidItemRequestIdThenReturnedItemRequest() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        ItemRequest itemRequest1 = itemRequestService.findById(10L);
        assertEquals(itemRequest1, itemRequest);
    }
}