package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validationmarkers.Create;
import ru.practicum.shareit.validationmarkers.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    public static final int MIN_ID_VALUE = 1;
    public static final String NULL_USER_ID = "userID is null";

    private final UserMapper mapper;
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        User user = mapper.toModel(userDto, null);
        return mapper.toDto(userService.createUser(user));
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@NotNull(message = (NULL_USER_ID))
                                @Min(MIN_ID_VALUE)
                                @PathVariable Long userId) {
        return mapper.toDto(userService.findUserById(userId));
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return mapper.mapUserListToUserDtoList(userService.findAllUsers());
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@NotNull(message = NULL_USER_ID)
                              @Min(MIN_ID_VALUE)
                              @PathVariable Long userId,
                              @Validated({Update.class})
                              @RequestBody UserDto userDto) {
        User user = mapper.toModel(userDto, userId);
        return mapper.toDto(userService.updateUser(userId, user));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@NotNull(message = (NULL_USER_ID))
                               @Min(MIN_ID_VALUE)
                               @PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}