package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final ItemService itemService;

    private final UserService userService;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  ItemService itemService,
                                  UserService userService) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestOutputDto getItemRequestById(Long itemRequestId, Long userId) {
        log.info("ItemRequestService.getItemRequestById: {} {} - Started", itemRequestId, userId);
        userService.findById(userId);
        ItemRequest itemRequest = findById(itemRequestId);
        List<ItemDto> items = itemService.getItemsByRequestId(itemRequest.getId());
        log.info("ItemRequestService.getItemRequestById: {} - Finished", itemRequest);
        return ItemRequestMapper.toItemRequestOutputDto(itemRequest, items);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestOutputDto> getItemRequestsByOtherUsers(Long userId, Integer from, Integer size) {
        log.info("ItemRequestService.getItemRequestsByOtherUsers: {} - Started", userId);
        User user = userService.findById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository
                .findByRequesterNotInOrderByCreatedDesc(List.of(user), PageRequest.of(from / size, size));
        List<ItemRequestOutputDto> itemRequestOutputDtos = itemRequests.stream()
                .map(itemRequest ->
                        ItemRequestMapper.toItemRequestOutputDto(
                                itemRequest,
                                itemService.getItemsByRequestId(itemRequest.getId())))
                .collect(Collectors.toList());
        log.info("ItemRequestService.getItemRequestsByOtherUsers: {} - Finished", itemRequestOutputDtos.size());
        return itemRequestOutputDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestOutputDto> getItemRequests(Long userId) {
        log.info("ItemRequestService.getItemRequests: {} - Started", userId);
        User user = userService.findById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterOrderByCreatedDesc(user);
        List<ItemRequestOutputDto> itemRequestOutputDtos = itemRequests
                .stream()
                .map(itemRequest ->
                        ItemRequestMapper.toItemRequestOutputDto(
                                itemRequest,
                                itemService.getItemsByRequestId(itemRequest.getId())))
                .collect(Collectors.toList());
        log.info("ItemRequestService.getItemRequests: {} - Finished", itemRequestOutputDtos.size());
        return itemRequestOutputDtos;
    }

    @Override
    public ItemRequestOutputDto create(Long userId, ItemRequestInputDto itemRequestInputDto) {
        log.info("ItemRequestService.create: {} {} - Started", userId, itemRequestInputDto);
        User requester = userService.findById(userId);

        ItemRequest itemRequest = itemRequestRepository.save(
                ItemRequestMapper.toItemRequest(itemRequestInputDto, requester));
        List<ItemDto> items = itemService.getItemsByRequestId(itemRequest.getId());
        log.info("ItemRequestService.getItemRequests: {} - Finished", itemRequest);
        return ItemRequestMapper.toItemRequestOutputDto(itemRequest, items);
    }

    @Transactional(readOnly = true)
    public ItemRequest findById(Long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(
                        () -> new ItemRequestNotFoundException(
                                String.format("Запрос с ID : %s не найден", id))
                        );

    }
}
