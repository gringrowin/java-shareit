package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();

    User add(User user);

    User update(User user);

    User getUser(Long id);

    void deleteUser(Long userId);
}
