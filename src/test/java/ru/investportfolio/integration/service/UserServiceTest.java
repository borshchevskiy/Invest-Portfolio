package ru.investportfolio.integration.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.database.repository.UserRepository;
import ru.investportfolio.dto.UserCreateDTO;
import ru.investportfolio.dto.UserEditDTO;
import ru.investportfolio.integration.IntegrationTestBase;
import ru.investportfolio.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
class UserServiceTest extends IntegrationTestBase {

    private final UserRepository userRepository;
    private final UserService userService;
    private static final String TEST_USERNAME = "test@test.com";

    @Test
    void loadUserByUsername() {
        var actual = userService.loadUserByUsername(TEST_USERNAME);
        assertEquals(TEST_USERNAME, actual.getUsername());
    }
    @Test
    void loadUserByUsernameWithException() {
        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("faultyUserName"));
    }

    @Test
    void findAll() {
        List<User> all = userService.findAll();
        assertThat(all).hasSize(2);
    }
    @Test
    void findById() {
        User userById = userService.findById(1L);
        String expectedUsername = "admin@admin.com";
        assertEquals(expectedUsername, userById.getUsername());
    }
    @Test
    void findByIdWithException() {
        assertThrows(UsernameNotFoundException.class,
                () -> userService.findById(999L));
    }
    @Test

    void create() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail("test2@test.com");
        userCreateDTO.setPassword("testPassword");
        boolean createResult = userService.create(userCreateDTO);
        assertTrue(createResult);
    }
    @Test
    void createEmailExistsError() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail("test@test.com");
        userCreateDTO.setPassword(TEST_USERNAME);
        boolean createResult = userService.create(userCreateDTO);
        assertFalse(createResult);
    }
    @Test

    void update() {
        User testUser = userRepository.findByEmail(TEST_USERNAME).get();
        UserEditDTO userEditDTO = new UserEditDTO();
        userEditDTO.setEmail(testUser.getEmail());
        userEditDTO.setFirstname("John");
        userEditDTO.setLastname("Smith");
        User updatedUser = userService.update(testUser, userEditDTO);
        assertEquals("John", updatedUser.getFirstname());
    }
    @Test

    void updatePassword() {
        String oldPassword = userRepository.findByEmail(TEST_USERNAME).get().getPassword();
        User testUser = new User();
        testUser.setEmail(TEST_USERNAME);
        userService.updatePassword(testUser, "newPassword");
        String updatedPassword = userRepository.findByEmail(TEST_USERNAME).get().getPassword();
        assertNotNull(updatedPassword);
        assertNotEquals(oldPassword, updatedPassword);
    }
    @Test

    void deleteSuccess() {
        Long id = userRepository.findByEmail(TEST_USERNAME).get().getId();
        boolean deleteResult = userService.delete(id);
        assertTrue(deleteResult);
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(TEST_USERNAME));
    }
    @Test
    void deleteFail() {
        boolean deleteResult = userService.delete(Long.MAX_VALUE);
        assertFalse(deleteResult);
    }
}