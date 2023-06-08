package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserMapper mapper;
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody User user) {
        return mapper.toDto(userService.createUser(user));
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable Long userId) {
        return mapper.toDto(userService.findUserById(userId));
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return mapper.mapUserListToUserDtoList(userService.findAllUsers());
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody User user) {
        return mapper.toDto(userService.updateUser(userId, user));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}