package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<User> getAll();

    UserDto create(User user);

    UserDto update(Long id, UserDto userDto);

    UserDto getUser(Long id);

    void deleteUser(Long userId);
}
