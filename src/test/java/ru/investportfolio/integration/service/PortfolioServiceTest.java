package ru.investportfolio.integration.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import ru.investportfolio.database.entity.CashAction;
import ru.investportfolio.database.entity.Portfolio;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.database.repository.PortfolioRepository;
import ru.investportfolio.dto.CashEditDTO;
import ru.investportfolio.exception.ItemNotFoundException;
import ru.investportfolio.integration.IntegrationTestBase;
import ru.investportfolio.service.PortfolioService;
import ru.investportfolio.service.PositionService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
class PortfolioServiceTest extends IntegrationTestBase {

    private static final Long ADMIN_TEST_PORTFOLIO_ID = 1L;
    private static final Long ADMIN_TEST_PORTFOLIO_ID_2 = 2L;
    private static final Integer ADMIN_TEST_PORTFOLIOS_QUANTITY = 2;
    private static final Long ADMIN_TEST_ID = 1L;
    private static final Long FAULTY_ID = Long.MAX_VALUE;

    private final PortfolioService portfolioService;
    private final PositionService positionService;
    private final PortfolioRepository portfolioRepository;
    @Mock
    private SecurityContext mockSecurityContext;
    @Mock
    private Authentication mockAuthentication;

    @Test
    void findAllByUser() {
        User testUser = new User();
        testUser.setId(ADMIN_TEST_ID);
        Set<Portfolio> actualResult = portfolioService.findAllByUser(testUser);
        assertThat(actualResult).hasSize(ADMIN_TEST_PORTFOLIOS_QUANTITY);
    }
    @Test
    void findByIdSuccess() {
        Portfolio actualResult = portfolioService.findById(ADMIN_TEST_PORTFOLIO_ID);
        assertThat(actualResult.getName()).isEqualTo("adminPortfolio");
    }
    @Test
    void findByIdWithException() {
        assertThrows(ItemNotFoundException.class, () -> portfolioService.findById(FAULTY_ID));
    }
    @Test
    void updateFinancialResult() {
        Portfolio testPortfolio = portfolioService.findById(ADMIN_TEST_PORTFOLIO_ID);
        assertThat(testPortfolio.getPositionsValue()).isZero();
        assertThat(testPortfolio.getTotalValue()).isZero();
        assertThat(testPortfolio.getProfitLoss()).isZero();
        assertThat(testPortfolio.getProfitLossPercentage()).isZero();
        portfolioService.updateFinancialResult(testPortfolio);
        assertThat(testPortfolio.getPositionsValue()).isNotZero();
        assertThat(testPortfolio.getTotalValue()).isNotZero();
        assertThat(testPortfolio.getProfitLoss()).isNotZero();
        assertThat(testPortfolio.getProfitLossPercentage()).isNotZero();

    }
    @Test
    @DirtiesContext
    void create() {
        User testUser = new User();
        testUser.setId(ADMIN_TEST_ID);
        SecurityContextHolder.setContext(mockSecurityContext);
        Mockito.doReturn(mockAuthentication).when(mockSecurityContext).getAuthentication();
        Mockito.doReturn(testUser).when(mockAuthentication).getPrincipal();
        Portfolio actualPortfolio = portfolioService.create("adminPortfolio2");
        assertThat(actualPortfolio).isNotNull();
        assertThat(actualPortfolio.getId()).isNotNull();
        assertThat(actualPortfolio.getName()).isEqualTo("adminPortfolio2");
    }
    @Test
    void updateSuccess() {
        String newName = "newPortfolioName";
        Portfolio actualPortfolio = portfolioService.update(newName, ADMIN_TEST_PORTFOLIO_ID);
        assertThat(actualPortfolio.getName()).isEqualTo(newName);
    }
    @Test
    void updateWithException() {
        assertThrows(ItemNotFoundException.class,
                () -> portfolioService.update("newPortfolioName", FAULTY_ID));
    }
    @Test
    void deleteSuccess() {
        boolean deleteResult = portfolioService.delete(ADMIN_TEST_PORTFOLIO_ID_2);
        assertTrue(deleteResult);
    }
    @Test
    void deleteFail() {
        boolean deleteResult = portfolioService.delete(FAULTY_ID);
        assertFalse(deleteResult);
    }
    @Test
    void updateCashAdd() {
        BigDecimal startingCash = portfolioRepository.findById(ADMIN_TEST_PORTFOLIO_ID).get().getCash();
        CashEditDTO cashEditDTO = new CashEditDTO();
        BigDecimal extraCash = BigDecimal.valueOf(10000);
        cashEditDTO.setCash(extraCash);
        cashEditDTO.setCashAction(CashAction.ADD);
        boolean updateResult = portfolioService.updateCash(ADMIN_TEST_PORTFOLIO_ID, cashEditDTO);
        assertTrue(updateResult);
        BigDecimal actualCash = portfolioRepository.findById(ADMIN_TEST_PORTFOLIO_ID).get().getCash();
        assertEquals(startingCash.add(extraCash), actualCash);
    }

    @Test
    void updateCashRemove() {
        BigDecimal startingCash = portfolioRepository.findById(ADMIN_TEST_PORTFOLIO_ID).get().getCash();
        CashEditDTO cashEditDTO = new CashEditDTO();
        BigDecimal extraCash = BigDecimal.valueOf(10000);
        cashEditDTO.setCash(extraCash);
        cashEditDTO.setCashAction(CashAction.REMOVE);
        boolean updateResult = portfolioService.updateCash(ADMIN_TEST_PORTFOLIO_ID, cashEditDTO);
        assertTrue(updateResult);
        BigDecimal actualCash = portfolioRepository.findById(ADMIN_TEST_PORTFOLIO_ID).get().getCash();
        assertEquals(startingCash.subtract(extraCash), actualCash);
    }

    @Test
    void updateCashFail() {
        BigDecimal startingCash = portfolioRepository.findById(ADMIN_TEST_PORTFOLIO_ID).get().getCash();
        CashEditDTO cashEditDTO = new CashEditDTO();
        BigDecimal extraCash = BigDecimal.valueOf(1000000);
        cashEditDTO.setCash(extraCash);
        cashEditDTO.setCashAction(CashAction.REMOVE);
        boolean updateResult = portfolioService.updateCash(ADMIN_TEST_PORTFOLIO_ID, cashEditDTO);
        assertFalse(updateResult);
    }
}