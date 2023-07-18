package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto dto);

    UserDto updateUser(long userId, UserDto dto);

    UserDto findUserById(long userId);

    void deleteUserById(long userId);

    List<UserDto> findAllUsers();
}