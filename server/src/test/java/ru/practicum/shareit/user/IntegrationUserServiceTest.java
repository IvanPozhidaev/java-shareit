package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationUserServiceTest {
    private final EntityManager em;
    private final UserService service;

    @Test
    public void saveUserTest() {
        UserDto userDto = generateUserDto();
        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from users u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertNotNull(user.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        service.deleteUserById(user.getId());
    }

    @Test
    public void findUserByIdTest() {
        UserDto savedUser = service.createUser(generateUserDto());
        UserDto userDto = service.findUserById(savedUser.getId());
        assertThat(userDto.getId(), notNullValue());
        assertThat(userDto.getName(), equalTo("user"));
        assertThat(userDto.getEmail(), equalTo("user@email.com"));
        service.deleteUserById(userDto.getId());
    }

    private UserDto generateUserDto() {
        UserDto dto = new UserDto();
        dto.setEmail("user@email.com");
        dto.setName("user");
        return dto;
    }
}