package ru.investportfolio.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.database.repository.UserRepository;
import ru.investportfolio.dto.UserCreateDTO;
import ru.investportfolio.dto.UserEditDTO;
import ru.investportfolio.dto.mapper.UserCreateMapper;
import ru.investportfolio.dto.mapper.UserEditMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long NO_USER_ID = 0L;
    private static final String TEST_EMAIL = "test@test.com";
    private static final String EMPTY_TEST_EMAIL = "";
    private static final User TEST_USER = new User("test@test.com", "password",
            true, Collections.emptySet());
    private static final User TEST_USER_2 = new User("test2@test.com", "password2",
            true, Collections.emptySet());

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserCreateMapper userCreateMapper;
    @Mock
    private UserEditMapper userEditMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    class Retrieve {
        @Test
        void loadUserByEmail() {
            doReturn(Optional.of(new User(TEST_EMAIL, "password", true, Collections.emptySet())))
                    .when(userRepository).findByEmail(TEST_EMAIL);
            UserDetails actualResult = userService.loadUserByUsername(TEST_EMAIL);
            assertNotNull(actualResult);
            assertEquals(actualResult.getUsername(), TEST_EMAIL);
        }

        @Test
        void loadUserByEmailWithException() {
            assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(EMPTY_TEST_EMAIL));
        }

        @Test
        void findAll() {
            doReturn(List.of(TEST_USER, TEST_USER_2)).when(userRepository).findAll();
            var actualResult = userService.findAll();
            assertNotNull(actualResult);
            assertThat(actualResult).hasSize(2);
        }

        @Test
        void findById() {
            doReturn(Optional.of(TEST_USER)).when(userRepository).findById(USER_ID);
            User actualResult = userService.findById(1L);
            assertNotNull(actualResult);
            User expectedResult = new User("test@test.com", "", true, Collections.emptySet());
            assertEquals(actualResult.getEmail(), expectedResult.getEmail());
        }
        @Test
        void findByIdNoUserFound() {
            assertThrows(UsernameNotFoundException.class, () -> userService.findById(5L));
        }
    }

    @Nested
    class Create {
        @Test
        void newUser() {
            doReturn(Optional.empty()).when(userRepository).findByEmail(TEST_EMAIL);
            var userDTO = new UserCreateDTO();
            userDTO.setEmail(TEST_EMAIL);
            userDTO.setPassword("password");
            var actualResult = userService.create(userDTO);
            assertTrue(actualResult);

        }
        @Test
        void userExists() {
            doReturn(Optional.of(TEST_USER)).when(userRepository).findByEmail(TEST_EMAIL);
            var userDTO = new UserCreateDTO();
            userDTO.setEmail(TEST_EMAIL);
            userDTO.setPassword("password");
            var actualResult = userService.create(userDTO);
            assertFalse(actualResult);
        }
    }

    @Nested
    class Update {
        @Test
        void update() {
            User user = new User("test@test.com", "password", true, Collections.emptySet());
            User updatedUser = new User("a@a.com", "password", true, Collections.emptySet());
            var userDTO = new UserEditDTO();
            userDTO.setEmail("a@a.com");
            doReturn(updatedUser).when(userRepository).saveAndFlush(updatedUser);
            doReturn(updatedUser).when(userEditMapper).map(userDTO, user);
            var actualResult = userService.update(user, userDTO);
            assertEquals(actualResult, updatedUser);
        }

        @Test
        void updatePassword() {
            User user = new User("test@test.com", "password", true, Collections.emptySet());
            doReturn(Optional.of(user)).when(userRepository).findByEmail(TEST_EMAIL);
            doReturn("new password encrypted").when(passwordEncoder).encode("new password");
            doReturn(user).when(userRepository).save(user);
            userService.updatePassword(user, "new password");
            assertEquals(user.getPassword(), "new password encrypted");
        }
    }

    @Nested
    class Delete {
        @Test
        void deleteTrue() {
            doReturn(Optional.of(TEST_USER)).when(userRepository).findById(USER_ID);
            assertTrue(userService.delete(USER_ID));

        }

        @Test
        void deleteFalseWhenUserNotFound() {
            doReturn(Optional.empty())
                    .when(userRepository).findById(NO_USER_ID);
            assertFalse(userService.delete(NO_USER_ID));
        }
    }
}