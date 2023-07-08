package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.MarkerUserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") Long id) {
        log.info("UserController.getUser: {} id - Started", id);
        UserDto userDto = userService.getUser(id);
        log.info("UserController.getUser: {} - Finished", userDto);
        return userDto;
    }

    @PostMapping
    public UserDto create(@Validated(MarkerUserDto.OnCreate.class) @RequestBody UserDto userDto) {
        log.info("UserController.create: {} - Started", userDto);
        userDto = userService.create(userDto);
        log.info("UserController.create: {} - Finished", userDto);
        return userDto;
    }

    @PatchMapping("/{userId}")
    public UserDto update(
            @PathVariable("userId") Long id,
            @Validated(MarkerUserDto.OnUpdate.class) @RequestBody UserDto userDto) {
        log.info("UserController.update: {} {} - Started", id, userDto);
        userDto = userService.update(id, userDto);
        log.info("UserController.update: {} - Finished", userDto);
        return userDto;
    }

    @DeleteMapping("/{userId}")
    public Long delete(@PathVariable("userId") Long id) {
        log.info("UserController.delete: {} - Started", id);
        userService.deleteUser(id);
        log.info("UserController.delete: {} - Finished", id);
        return id;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("UserController.getAll: - Started");
        List<UserDto> users = userService.getAll();
        log.info("UserController.getAll: {} - Finished", users.size());
        return users;
    }

}
