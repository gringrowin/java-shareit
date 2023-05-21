package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserStorageInMemory implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long idGenerator = 1L;



    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User add(User user) {
        user.setId(getNewId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    private Long getNewId() {
        return idGenerator++;
    }
}
