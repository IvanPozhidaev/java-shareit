package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toModel(userDto, null);
        user = userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = patchUser(userId, userDto);
        user = userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto findUserById(long userId) {
        return UserMapper.toDto(userRepository.findById(userId).orElseThrow());
    }

    @Override
    @Transactional
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return UserMapper.mapUserListToUserDtoList(userRepository.findAll());
    }

    private User patchUser(Long userId, UserDto patch) {
        UserDto entry = findUserById(userId);
        String name = patch.getName();
        if (name != null && !name.isBlank()) {
            entry.setName(name);
        }

        String oldEmail = entry.getEmail();
        String newEmail = patch.getEmail();
        if (newEmail != null && !newEmail.isBlank() && !oldEmail.equals(newEmail)) {
            entry.setEmail(newEmail);
        }
        return UserMapper.toModel(entry, userId);
    }
}