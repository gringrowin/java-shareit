package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ItemStorageInMemory implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    private Long idGenerator = 1L;


    @Override
    public Item getItem(Long id) {
        return items.get(id);
    }

    @Override
    public Item addItem(Item item) {
        item.setId(getNewId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(
                        (((Predicate<Item>) item -> item.getName().toLowerCase().contains(text.toLowerCase()))
                                .or(item -> item.getDescription().toLowerCase().contains(text.toLowerCase())))
                                .and(Item::getAvailable))
                .collect(Collectors.toList());
    }

    private Long getNewId() {
        return idGenerator++;
    }

}
