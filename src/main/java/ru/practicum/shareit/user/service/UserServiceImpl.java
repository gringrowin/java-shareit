package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        log.info("UserService.getAll: {}", users.size());
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long id) {
        log.info("UserService.getUser id : {} - Started", id);
        User user = findById(id);
        log.info("UserService.getUser: {} - Finished", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        log.info("UserService.create: {} - Started", userDto);
        User user = UserMapper.toUser(userDto);
        user = userRepository.save(user);
        log.info("UserService.create: {} - Finished", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        log.info("UserService.update: {} {} - Started", id, userDto);
        User user = userRepository.save(UserMapper.toUser(findById(id), userDto));
        log.info("UserService.update: {} - Finished", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("UserService.deleteUser: {} ", id);
        userRepository.delete(findById(id));
    }

    public User findById(Long id) {
        log.info("UserService.findById: {} ", id);
        return  userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                String.format("Пользователь с ID : %s не найден", id))
                );
    }
}
