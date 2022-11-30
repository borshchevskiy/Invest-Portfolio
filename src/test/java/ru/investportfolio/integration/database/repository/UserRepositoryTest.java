package ru.investportfolio.integration.database.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.investportfolio.database.repository.UserRepository;
import ru.investportfolio.integration.IntegrationTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
class UserRepositoryTest extends IntegrationTestBase {

    private final UserRepository userRepository;

    @Test
    void findByEmail() {
        var actual = userRepository.findByEmail("admin@admin.com");
        assertTrue(actual.isPresent());
        assertEquals("admin@admin.com", actual.get().getEmail());
    }
}