package ru.investportfolio.integration.database.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.database.repository.PortfolioRepository;
import ru.investportfolio.integration.IntegrationTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
class PortfolioRepositoryTest extends IntegrationTestBase {

    private final PortfolioRepository portfolioRepository;
    private static final User TEST_USER = new User();

    @BeforeAll
    static void prepare(){
        TEST_USER.setId(1L);
        TEST_USER.setEmail("admin@admin.com");
    }

    @Test
    void findAllByUser() {
        var result = portfolioRepository.findAllByUser(TEST_USER);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(1, result.get().stream().filter(portfolio -> portfolio.getId()==9).count());
    }
}