package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toModel(UserDto userDto, Long userId) {
        return new User(userId, userDto.getName(), userDto.getEmail());
    }


    public static List<UserDto> mapUserListToUserDtoList(List<User> users) {
        List<UserDto> result = new ArrayList<>();
        for (User user : users) {
            result.add(toDto(user));
        }
        return result;
    }
}