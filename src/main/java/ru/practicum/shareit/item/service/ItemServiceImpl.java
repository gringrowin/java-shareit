package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidCommentException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.mapper.CommentMapper;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.comments.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    @Override
    public ItemOutputDto getItem(Long id, Long userId) {
        log.info("ItemService.getItem id : {} - Started", id);
        Item item = findById(id);
        Booking lastBooking = getLastBooking(item.getId());
        Booking nextBooking = getNextBooking(item.getId());
        ItemOutputDto itemOutputDto = ItemMapper.toItemOutputDto(
                item,
                userId,
                lastBooking,
                nextBooking,
                getCommentsByItemId(id)
        );
        log.info("ItemService.getItem id : {} - Finished", itemOutputDto);
        return itemOutputDto;
    }


    @Override
    public ItemOutputDto create(Long userId, ItemInputDto itemInputDto) {
        log.info("ItemService.create: {} - Started", itemInputDto);
        User user = userService.findById(userId);
        Item item = ItemMapper.toItem(itemInputDto, user);
        item = itemRepository.save(item);
        log.info("ItemService.create: {} - Finished", item);
        return ItemMapper.toItemOutputDto(
                item,
                userId,
                getLastBooking(item.getId()),
                getNextBooking(item.getId()),
                getCommentsByItemId(item.getId())
        );
    }

    @Override
    @Transactional
    public ItemOutputDto update(Long userId, Long itemId, ItemInputDto itemInputDto) {
        log.info("ItemService.update: {} {} {} - Started", userId, itemId, itemInputDto);
        Item item = findById(itemId);
        User user = userService.findById(userId);
        if (!user.equals((item.getOwner()))) {
            throw new ItemNotFoundException(
                    String.format("Пользователь с ID %s не является владельцем вещи", userId)
            );
        }
        if (itemInputDto.getName() != null) {
            item.setName(itemInputDto.getName());
        }

        if (itemInputDto.getDescription() != null) {
            item.setDescription(itemInputDto.getDescription());
        }

        if (itemInputDto.getAvailable() != null) {
            item.setAvailable(itemInputDto.getAvailable());
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

    public Item findById(Long id) {
        log.info("ItemService.findById id : {}", id);
        return itemRepository.findById(id)
                .orElseThrow(
                        () -> new ItemNotFoundException(
                                String.format("Вещь с ID : %s не найдена", id))
                );
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userService.findById(userId);

        commentDto.setAuthorName(user.getName());

        LocalDateTime created = LocalDateTime.now();
        commentDto.setCreated(created);
        commentDto.setItemId(itemId);

        Item item = findById(itemId);

        List<Booking> userBookingList = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                userId, LocalDateTime.now());

        if (userBookingList.isEmpty()) {
            throw new ItemNotAvailableException("Пользователь ни разу не бронировал вещь");
        }
        if (commentDto.getText().isEmpty()) {
            throw new InvalidCommentException("Пустой коментарий");
        }

        return CommentMapper.toDto(commentRepository.save(CommentMapper.toEntity(commentDto, user, item)));
    }

    private Booking getNextBooking(Long id) {
        return bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(id, LocalDateTime.now());
    }

    private Booking getLastBooking(Long id) {
        return bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(id, LocalDateTime.now());
    }

    private List<CommentDto> getCommentsByItemId (Long itemId) {
        return commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }
}
