package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    @Override
    public List<User> getAll() {
        List<User> users = userStorage.getAll();
        log.info("UserService.getAll: {}", users.size());
        return users;
    }

    @Override
    public UserDto getUser(Long id) {
        log.info("UserService.getUser id : {} - Started", id);
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new UserNotFoundException(String.format("Пользователь с ID : %s не найден", id));
        }
        log.info("UserService.getUser: {} - Finished", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(User user) {
        log.info("UserService.create: {} - Started", user);
        checkEmailUser(user.getEmail());
        user = userStorage.add(user);
        log.info("UserService.create: {} - Finished", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        log.info("UserService.update: {} {} - Started", id, userDto);

        User user = userStorage.getUser(id);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null
                && !userDto.getEmail().equals(user.getEmail())) {

            checkEmailUser(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }

        userStorage.update(user);
        log.info("UserService.update: {} - Finished", user);

        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("UserService.deleteUser: {} ", id);
        userStorage.deleteUser(id);
    }

    private void checkEmailUser(String email) {
        List<String> emails = getAll()
                .stream()
                .map(User::getEmail)
                .collect(Collectors.toList());

        if (emails.contains(email)) {
            throw new DuplicateEmailException("Такой Email уже существует");
        }
    }
}
