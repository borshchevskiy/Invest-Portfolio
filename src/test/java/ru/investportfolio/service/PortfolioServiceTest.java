package ru.investportfolio.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.investportfolio.database.entity.*;
import ru.investportfolio.database.repository.PortfolioRepository;
import ru.investportfolio.dto.CashEditDTO;
import ru.investportfolio.exception.ItemNotFoundException;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    private static final User TEST_USER = new User("test@test.com", "password",
            true, Collections.emptySet());
    private static final Portfolio USER_PORTFOLIO_1 = new Portfolio("portfolio_1", TEST_USER);
    private static final Portfolio USER_PORTFOLIO_2 = new Portfolio("portfolio_2", TEST_USER);
    private static final Portfolio TEST_PORTFOLIO = new Portfolio();
    private static final List<Position> TEST_POSITIONS = new ArrayList<>();

    @Mock
    private PositionService positionService;
    @Mock
    private PortfolioRepository portfolioRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private PortfolioService portfolioService;

    @BeforeAll
    static void prepare() {
        USER_PORTFOLIO_1.setId(1L);
        USER_PORTFOLIO_2.setId(2L);
        TEST_PORTFOLIO.setId(1L);
        TEST_PORTFOLIO.setName("portfolio");
        TEST_PORTFOLIO.setUser(TEST_USER);
        TEST_PORTFOLIO.setCash(BigDecimal.valueOf(100));
        Position position1 = new Position("Position1", "POS1",
                BigDecimal.valueOf(100), PositionType.LONG,
                1L, new ArrayList<>(), TEST_PORTFOLIO);
        Position position2 = new Position("Position2", "POS2",
                BigDecimal.valueOf(200), PositionType.LONG,
                2L, new ArrayList<>(), TEST_PORTFOLIO);
        Position position3 = new Position("Position3", "POS3",
                BigDecimal.valueOf(300), PositionType.LONG,
                3L, new ArrayList<>(), TEST_PORTFOLIO);
        position1.setAcquisitionValue(BigDecimal.valueOf(100));
        position2.setAcquisitionValue(BigDecimal.valueOf(400));
        position3.setAcquisitionValue(BigDecimal.valueOf(900));
        TEST_POSITIONS.add(position1);
        TEST_POSITIONS.add(position2);
        TEST_POSITIONS.add(position3);
        TEST_PORTFOLIO.setPositions(TEST_POSITIONS);
    }

    @AfterEach
    void afterEach() {
        TEST_PORTFOLIO.setId(1L);
        TEST_PORTFOLIO.setName("portfolio");
        TEST_PORTFOLIO.setUser(TEST_USER);
        TEST_PORTFOLIO.setCash(BigDecimal.valueOf(100));
        TEST_PORTFOLIO.setPositions(TEST_POSITIONS);
    }

    @Nested
    class Retrieve {
        @Test
        void findAllByUser() {
            Set<Portfolio> portfolios = new HashSet<>(Set.of(USER_PORTFOLIO_1, USER_PORTFOLIO_2));
            doReturn(Optional.of(portfolios)).when(portfolioRepository).findAllByUser(TEST_USER);
            var actualResult = portfolioService.findAllByUser(TEST_USER);
            assertThat(actualResult).hasSameElementsAs(portfolios);
        }
        @Test
        void findByUserNoPortfolios() {
            doReturn(Optional.empty()).when(portfolioRepository).findAllByUser(TEST_USER);
            var actualResult = portfolioService.findAllByUser(TEST_USER);
            assertThat(actualResult).isEmpty();
        }
        @Test
        void findById() {
            doReturn(Optional.of(USER_PORTFOLIO_1)).when(portfolioRepository).findById(1L);
            Portfolio actualResult = portfolioService.findById(1L);
            assertEquals("portfolio_1", actualResult.getName());
        }
        @Test
        void findByIdWithException() {
            assertThrows(ItemNotFoundException.class, () -> portfolioService.findById(0L));
        }
    }

    @Nested
    class Create {
        @Test
        void create() {
            SecurityContextHolder.setContext(securityContext);
            doReturn(authentication).when(securityContext).getAuthentication();
            doReturn(TEST_USER).when(authentication).getPrincipal();
            doReturn(USER_PORTFOLIO_1).when(portfolioRepository).save(any(Portfolio.class));
            var actualUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var actualResult = portfolioService.create("portfolio");
            assertAll(() -> {
                        assertEquals(TEST_USER, actualUser);
                        assertEquals(USER_PORTFOLIO_1, actualResult);
                    }
            );
        }
    }

    @Nested
    class Update {
        @Test
        void updateFinancialResultPrivateMethods() {
            TEST_PORTFOLIO.getPositions().forEach(position ->
                    position.setLiquidationValue(position.getAcquisitionValue()));
            Collections.shuffle(TEST_PORTFOLIO.getPositions());
            portfolioService.updateFinancialResult(TEST_PORTFOLIO);
            assertAll(() -> {
                assertEquals("Position1", TEST_PORTFOLIO.getPositions().get(0).getSecurityName());
                assertEquals("Position2", TEST_PORTFOLIO.getPositions().get(1).getSecurityName());
                assertEquals("Position3", TEST_PORTFOLIO.getPositions().get(2).getSecurityName());
                assertEquals(BigDecimal.valueOf(1400), TEST_PORTFOLIO.getPositionsValue());
                assertEquals(BigDecimal.valueOf(1500), TEST_PORTFOLIO.getTotalValue());
            });
        }
        @Test
        void updateFinancialResultEmpty() {
            TEST_PORTFOLIO.setPositions(new ArrayList<>());
            portfolioService.updateFinancialResult(TEST_PORTFOLIO);
            assertAll(() -> {
                assertThat(TEST_PORTFOLIO.getPositions()).isEmpty();
                assertEquals(BigDecimal.ZERO, TEST_PORTFOLIO.getPositionsValue());
                assertEquals(TEST_PORTFOLIO.getTotalValue(), TEST_PORTFOLIO.getCash());
                assertEquals(BigDecimal.ZERO, TEST_PORTFOLIO.getProfitLoss());
                assertEquals(0d, TEST_PORTFOLIO.getProfitLossPercentage());
            });
        }
        @Test
        void updateFinancialResultZeroProfitLoss() {
            TEST_PORTFOLIO.getPositions().forEach(position ->
                    position.setLiquidationValue(position.getAcquisitionValue()));
            portfolioService.updateFinancialResult(TEST_PORTFOLIO);

            assertAll(() -> {
                assertEquals(BigDecimal.ZERO, TEST_PORTFOLIO.getProfitLoss());
                assertEquals(0d, TEST_PORTFOLIO.getProfitLossPercentage());
            });
        }
        @Test
        void updateFinancialResultWithProfit() {
            TEST_PORTFOLIO.getPositions().get(0).setLiquidationValue(BigDecimal.valueOf(200));
            TEST_PORTFOLIO.getPositions().get(1).setLiquidationValue(BigDecimal.valueOf(700));
            TEST_PORTFOLIO.getPositions().get(2).setLiquidationValue(BigDecimal.valueOf(1200));

            BigDecimal expectedPositionsValue = TEST_PORTFOLIO.getPositions()
                    .stream()
                    .map(Position::getLiquidationValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal expectedAcquisitionValue = TEST_PORTFOLIO.getPositions().stream()
                    .map(Position::getAcquisitionValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal expectedProfitLoss = expectedPositionsValue
                    .subtract(expectedAcquisitionValue);

            portfolioService.updateFinancialResult(TEST_PORTFOLIO);

            assertAll(() -> {
                assertEquals(expectedProfitLoss, TEST_PORTFOLIO.getProfitLoss());
                assertEquals(50d, TEST_PORTFOLIO.getProfitLossPercentage());
            });
        }
        @Test
        void updateFinancialResultWithLoss() {
            TEST_PORTFOLIO.getPositions().get(0).setLiquidationValue(BigDecimal.valueOf(100));
            TEST_PORTFOLIO.getPositions().get(1).setLiquidationValue(BigDecimal.valueOf(200));
            TEST_PORTFOLIO.getPositions().get(2).setLiquidationValue(BigDecimal.valueOf(400));

            portfolioService.updateFinancialResult(TEST_PORTFOLIO);

            assertAll(() -> {
                assertEquals(BigDecimal.valueOf(-700), TEST_PORTFOLIO.getProfitLoss());
                assertEquals(-50d, TEST_PORTFOLIO.getProfitLossPercentage());
            });
        }
        @Test
        void updateName() {
            Portfolio testPortfolio = new Portfolio();
            testPortfolio.setId(1L);
            testPortfolio.setName("portfolio");
            doReturn(Optional.of(testPortfolio)).when(portfolioRepository).findById(1L);
            var actualResult = portfolioService.update("new_name", 1L);
            assertEquals(actualResult.getName(), "new_name");
        }
        @Test
        void updatePortfolioNotFound() {
            assertThrows(ItemNotFoundException.class, () -> portfolioService.update("new_name", 0L));
        }
        @Test
        void updateCashAdd() {
            CashEditDTO cashDTO = new CashEditDTO();
            cashDTO.setCashAction(CashAction.ADD);
            cashDTO.setCash(BigDecimal.valueOf(3.141));
            Portfolio testPortfolio = new Portfolio("portfolio", TEST_USER);
            testPortfolio.setId(1L);
            doReturn(Optional.of(testPortfolio)).when(portfolioRepository).findById(1L);
            var actualResult = portfolioService.updateCash(1L, cashDTO);
            assertAll(() -> {
                assertThat(testPortfolio.getCash()).isEqualTo(BigDecimal.valueOf(3.141));
                assertTrue(actualResult);
            });
        }
        @Test
        void updateCashWithdraw() {
            CashEditDTO cashDTO = new CashEditDTO();
            cashDTO.setCashAction(CashAction.REMOVE);
            cashDTO.setCash(BigDecimal.valueOf(1.55));
            Portfolio testPortfolio = new Portfolio("portfolio", TEST_USER);
            testPortfolio.setId(1L);
            testPortfolio.setCash(BigDecimal.valueOf(5.56));
            doReturn(Optional.of(testPortfolio)).when(portfolioRepository).findById(1L);
            var actualResult = portfolioService.updateCash(1L, cashDTO);
            assertAll(() -> {
                assertThat(testPortfolio.getCash()).isEqualTo(BigDecimal.valueOf(4.01));
                assertTrue(actualResult);
            });
        }
        @Test
        void updateCashOverdraw() {
            CashEditDTO cashDTO = new CashEditDTO();
            cashDTO.setCashAction(CashAction.REMOVE);
            cashDTO.setCash(BigDecimal.ONE);
            Portfolio testPortfolio = new Portfolio("portfolio", TEST_USER);
            testPortfolio.setId(1L);
            testPortfolio.setCash(BigDecimal.ZERO);
            doReturn(Optional.of(testPortfolio)).when(portfolioRepository).findById(1L);
            var actualResult = portfolioService.updateCash(1L, cashDTO);
            assertFalse(actualResult);
        }
    }

    @Nested
    class Delete {
        @Test
        void deleteUserFound() {
            doReturn(Optional.of(USER_PORTFOLIO_1)).when(portfolioRepository).findById(1L);
            var actualResult = portfolioService.delete(1L);
            assertTrue(actualResult);
        }
        @Test
        void deleteUserNotFound() {
            doReturn(Optional.empty()).when(portfolioRepository).findById(1L);
            var actualResult = portfolioService.delete(1L);
            assertFalse(actualResult);
        }
    }
}