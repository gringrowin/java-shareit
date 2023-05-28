package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();

    User add(User user);

    User getUser(Long id);

    void deleteUser(Long userId);

    User update(Long id, UserDto userDto);
}
