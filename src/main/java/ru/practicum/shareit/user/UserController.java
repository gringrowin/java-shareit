package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
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
    public UserDto create(@Valid @RequestBody User user) {
        log.info("UserController.create: {} - Started", user);
        UserDto userDto = userService.create(user);
        log.info("UserController.create: {} - Finished", userDto);
        return userDto;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable("userId") Long id, @Valid @RequestBody UserDto userDto) {
        log.info("UserController.update: {} {} - Started", id, userDto);
        userDto = userService.update(id, userDto);
        log.info("UserController.update: {} - Finished", userDto);
        return userDto;
    }

    @DeleteMapping("/{userId}")
    public Long delete (@PathVariable("userId") Long id) {
        log.info("UserController.delete: {} - Started", id);
        userService.deleteUser(id);
        log.info("UserController.delete: {} - Finished", id);
        return id;
    }

}
