package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidCommentException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.mapper.CommentMapper;
import ru.practicum.shareit.item.comments.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.booking.dto.ItemResponseBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           UserService userService,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemOutputDto getItem(Long itemId, Long userId) {
        log.info("ItemService.getItem id : {} - Started", itemId);
        Item item = findById(itemId);
        ItemOutputDto itemOutputDto = ItemMapper.toItemOutputDto(
                item,
                userId,
                getLastBooking(itemId),
                getNextBooking(itemId),
                getCommentsByItemId(itemId)
        );
        log.info("ItemService.getItem id : {} - Finished", itemOutputDto);
        return itemOutputDto;
    }


    @Override
    @Transactional
    public ItemOutputDto create(Long userId, ItemDto itemDto) {
        log.info("ItemService.create: {} - Started", itemDto);
        User user = userService.findById(userId);
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository
                    .findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ItemRequestNotFoundException(
                            String.format("Запрос с ID : %s не найден", itemDto.getRequestId())
                    ));
        }
        Item item = ItemMapper.toItem(itemDto, user, itemRequest);
        item = itemRepository.save(item);
        log.info("ItemService.create: {} - Finished", item);
        return ItemMapper.toItemOutputDto(
                item,
                userId,
                getLastBooking(item.getId()),
                getLastBooking(item.getId()),
                getCommentsByItemId(item.getId())
        );
    }

    @Override
    @Transactional
    public ItemOutputDto update(Long userId, Long itemId, ItemDto itemDto) {
        log.info("ItemService.update: {} {} {} - Started", userId, itemId, itemDto);
        Item item = findById(itemId);
        User user = userService.findById(userId);
        if (!user.equals((item.getOwner()))) {
            throw new ItemNotFoundException(
                    String.format("Пользователь с ID %s не является владельцем вещи", userId)
            );
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        item = itemRepository.save(item);
        log.info("ItemService.update: {} - Finished", item);
        return ItemMapper.toItemOutputDto(
                item,
                userId,
                getLastBooking(item.getId()),
                getNextBooking(item.getId()),
                getCommentsByItemId(itemId)
        );
    }

    @Override
    @Transactional
    public void deleteItem(Long id, Long userId) {
        log.info("ItemService.deleteItem: {} - Started", id);
        Item item = findById(id);
        if (item.getOwner().getId().equals(userId)) {
        itemRepository.delete(item);
        }
        log.info("ItemService.deleteItem: {} - Finished", id);
    }

    @Override
    public List<ItemOutputDto> getItemsByUserId(Long userId) {
        log.info("ItemService.getItemsByUserId: {} - Started", userId);
        List<Item> itemsByUserId = itemRepository.findByOwnerId(userId);
        log.info("ItemService.getItemsByUserId: {} - Finished", itemsByUserId.size());
        return itemsByUserId.stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(item -> ItemMapper.toItemOutputDto(
                        item,
                        userId,
                        getLastBooking(item.getId()),
                        getNextBooking(item.getId()),
                        getCommentsByItemId(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemOutputDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("ItemService.searchItems: {} - Started", text);
        List<Item> findItems = itemRepository.search(text);
        log.info("ItemService.searchItems: {} - Finished", findItems.size());
        return findItems.stream()
                .map(item -> ItemMapper.toItemOutputDto(
                        item,
                        item.getOwner().getId(),
                        getLastBooking(item.getId()),
                        getNextBooking(item.getId()),
                        getCommentsByItemId(item.getId())))
                .collect(Collectors.toList());
    }

    public Item findById(Long itemId) {
        log.info("ItemService.findById id : {}", itemId);
        return itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new ItemNotFoundException(
                                String.format("Вещь с ID : %s не найдена", itemId))
                );
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        log.info("ItemService.addComment: {}, {}. {} - Started", itemId, userId, commentDto);
        User user = userService.findById(userId);

        Item item = findById(itemId);

        List<Booking> userBookingList = bookingRepository
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());

        if (userBookingList.isEmpty()) {
            throw new ItemNotAvailableException("Пользователь ни разу не бронировал вещь");
        }
        if (commentDto.getText().isEmpty()) {
            throw new InvalidCommentException("Пустой коментарий");
        }
        log.info("ItemService.addComment: {} - Finished", commentDto);
        return CommentMapper.toCommentDto(
                commentRepository.save(CommentMapper.toComment(commentDto, user, item))
        );
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        return itemRepository.findItemsByItemRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private ItemResponseBookingDto getNextBooking(Long itemId) {
        Booking nextBooking = bookingRepository
                .findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now());
        if (nextBooking != null && nextBooking.getStatus() != BookingState.REJECTED) {
            return BookingMapper.toItemResponseBookingDto(nextBooking);
        }
        return null;
    }

    private ItemResponseBookingDto getLastBooking(Long itemId) {
        Booking lastBooking = bookingRepository
                .findFirstByItemIdAndStartBeforeOrderByStartDesc(itemId, LocalDateTime.now());
        if (lastBooking != null && lastBooking.getStatus() != BookingState.REJECTED) {
            return BookingMapper.toItemResponseBookingDto(lastBooking);
        }
        return null;
    }

    private List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
