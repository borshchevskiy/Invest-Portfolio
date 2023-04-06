package ru.investportfolio.integration.database.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.investportfolio.database.entity.Portfolio;
import ru.investportfolio.database.entity.Position;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.database.repository.PositionRepository;
import ru.investportfolio.integration.IntegrationTestBase;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
class PositionRepositoryTest extends IntegrationTestBase {

    private final PositionRepository positionRepository;
    private static final Portfolio TEST_PORTFOLIO = new Portfolio();

    @BeforeAll
    static void prepare() {
        TEST_PORTFOLIO.setId(1L);
    }

    @Test
    void findAllByPortfolio(){
        Optional<Set<Position>> result = positionRepository.findAllByPortfolio(TEST_PORTFOLIO);
        assertTrue(result.isPresent());
        Set<Position> positions = result.get();
        assertEquals(2, positions.size());
        assertEquals(1, positions.stream().filter(p->p.getTicker().equals("AGRO")).count());
        assertEquals(1, positions.stream().filter(p->p.getTicker().equals("GAZP")).count());
    }
}