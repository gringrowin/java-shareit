package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.MarkerUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping("{userId}")
    public ResponseEntity<Object> getUser(@PathVariable("userId") Long id) {
        log.info("Gateway.UserController.getUser: {} id - Started", id);
        return userClient.getUser(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(
            @Validated(MarkerUserDto.OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Gateway.UserController.create: {} - Started", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> update(
            @PathVariable("userId") Long id,
            @Validated(MarkerUserDto.OnUpdate.class) @RequestBody UserDto userDto) {
        log.info("Gateway.UserController.update: {} {} - Started", id, userDto);
        return userClient.update(id, userDto);
    }

    @DeleteMapping("{userId}")
    public void delete(@PathVariable("userId") Long id) {
        log.info("Gateway.UserController.delete: {} - Started", id);
        userClient.delete(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Gateway.UserController.getAll: - Started");
        return userClient.getAll();
    }
}
