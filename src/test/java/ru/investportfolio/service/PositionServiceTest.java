package ru.investportfolio.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.investportfolio.database.entity.*;
import ru.investportfolio.database.repository.PositionRepository;
import ru.investportfolio.exception.ItemNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;
    @Mock
    private PriceService priceService;

    private static final Portfolio TEST_PORTFOLIO = new Portfolio();
    private static final Position TEST_POSITION = new Position();
    private static final Deal TEST_DEAL = new Deal();

    @InjectMocks
    private PositionService positionService;

    @BeforeAll
    static void prepare() {
        TEST_PORTFOLIO.setName("Portfolio");
        TEST_PORTFOLIO.setUser(new User());
        TEST_PORTFOLIO.setPositions(new ArrayList<>(List.of(TEST_POSITION)));
        TEST_POSITION.setId(1L);
        TEST_POSITION.setSecurityName("Position");
        TEST_POSITION.setTicker("POS");
        TEST_POSITION.setAcquisitionPrice(BigDecimal.valueOf(100));
        TEST_POSITION.setPositionType(PositionType.LONG);
        TEST_POSITION.setQuantity(2L);
        TEST_POSITION.setDeals(new ArrayList<>());
        TEST_POSITION.setPortfolio(TEST_PORTFOLIO);
        TEST_POSITION.setAcquisitionValue(TEST_POSITION.getAcquisitionPrice()
                .multiply(BigDecimal.valueOf(TEST_POSITION.getQuantity())));
        TEST_POSITION.setTotalAcquisitionValue(TEST_POSITION.getAcquisitionValue());
        TEST_DEAL.setPosition(TEST_POSITION);
        TEST_DEAL.setDealType(DealType.BUY);
        TEST_DEAL.setQuantity(1L);
        TEST_DEAL.setAcquisitionPrice(BigDecimal.valueOf(100));
        TEST_DEAL.setAcquisitionValue(TEST_DEAL.getAcquisitionPrice()
                .multiply(BigDecimal.valueOf(TEST_DEAL.getQuantity())));
        TEST_DEAL.setTotalAcquisitionValue(TEST_DEAL.getAcquisitionValue());
    }

    @AfterEach
    void afterEach() {
        TEST_PORTFOLIO.setPositions(new ArrayList<>(List.of(TEST_POSITION)));
        TEST_POSITION.setAcquisitionPrice(BigDecimal.valueOf(100));
        TEST_POSITION.setPositionType(PositionType.LONG);
        TEST_POSITION.setQuantity(2L);
        TEST_POSITION.setDeals(new ArrayList<>());
        TEST_POSITION.setAcquisitionValue(TEST_POSITION.getAcquisitionPrice()
                .multiply(BigDecimal.valueOf(TEST_POSITION.getQuantity())));
        TEST_POSITION.setTotalAcquisitionValue(TEST_POSITION.getAcquisitionValue());
        TEST_DEAL.setDealType(DealType.BUY);
        TEST_DEAL.setQuantity(1L);
        TEST_DEAL.setAcquisitionPrice(BigDecimal.valueOf(100));
        TEST_DEAL.setAcquisitionValue(TEST_DEAL.getAcquisitionPrice()
                .multiply(BigDecimal.valueOf(TEST_DEAL.getQuantity())));
        TEST_DEAL.setTotalAcquisitionValue(TEST_DEAL.getAcquisitionValue());
    }

    @Nested
    class Retrieve {
        @Test
        void findById() {
            doReturn(Optional.of(TEST_POSITION)).when(positionRepository).findById(1L);
            var actualResult = positionService.findById(1L);
            assertEquals(actualResult, TEST_POSITION);
        }
        @Test
        void findByIdNoPositionFound() {
            assertThrows(ItemNotFoundException.class, () -> positionService.findById(0L));
        }
    }

    @Nested
    class Create {
        @Test
        void create() {
            doReturn(TEST_POSITION).when(positionRepository).save(TEST_POSITION);
            var actualResult = positionService.create(TEST_POSITION);
            assertEquals(actualResult, TEST_POSITION);
        }
        @Test
        void createAndFlush() {
            doReturn(TEST_POSITION).when(positionRepository).saveAndFlush(TEST_POSITION);
            var actualResult = positionService.createAndFlush(TEST_POSITION);
            assertEquals(actualResult, TEST_POSITION);
        }
    }

    @Nested
    class Delete {
        @Test
        void deleteUserFound() {
            doReturn(Optional.of(TEST_POSITION)).when(positionRepository).findById(1L);
            var actualResult = positionService.delete(1L);
            assertThat(TEST_PORTFOLIO.getPositions()).isEmpty();
            assertTrue(actualResult);
        }
        @Test
        void deleteUserNotFound() {
            doReturn(Optional.empty()).when(positionRepository).findById(1L);
            var actualResult = positionService.delete(1L);
            assertFalse(actualResult);
        }
    }

    @Nested
    class Update {
        @Test
        void addDealBuy() {
            TEST_DEAL.setAcquisitionPrice(BigDecimal.valueOf(250));
            TEST_DEAL.setAcquisitionValue(TEST_DEAL.getAcquisitionPrice()
                    .multiply(BigDecimal.valueOf(TEST_DEAL.getQuantity())));
            TEST_DEAL.setTotalAcquisitionValue(TEST_DEAL.getAcquisitionValue());
            doReturn(Optional.of(TEST_POSITION)).when(positionRepository).findById(1L);
            positionService.addDeal(TEST_DEAL);
            assertAll(() -> {
                assertEquals(3L, TEST_POSITION.getQuantity());
                assertEquals(BigDecimal.valueOf(450), TEST_POSITION.getAcquisitionValue());
                assertEquals(BigDecimal.valueOf(450), TEST_POSITION.getTotalAcquisitionValue());
                assertEquals(PositionType.LONG, TEST_POSITION.getPositionType());
                assertEquals(BigDecimal.valueOf(150), TEST_POSITION.getAcquisitionPrice());
            });
        }
        @Test
        void addDealSellPartial() {
            TEST_DEAL.setDealType(DealType.SELL);
            doReturn(Optional.of(TEST_POSITION)).when(positionRepository).findById(1L);
            positionService.addDeal(TEST_DEAL);
            assertAll(() -> {
                assertEquals(1L, TEST_POSITION.getQuantity());
                assertEquals(BigDecimal.valueOf(100), TEST_POSITION.getAcquisitionValue());
                assertEquals(BigDecimal.valueOf(100), TEST_POSITION.getTotalAcquisitionValue());
                assertEquals(PositionType.LONG, TEST_POSITION.getPositionType());
                assertEquals(BigDecimal.valueOf(100), TEST_POSITION.getAcquisitionPrice());
            });
        }
        @Test
        void addDealBuyAndClosePosition() {
            TEST_POSITION.setPositionType(PositionType.SHORT);
            TEST_POSITION.setQuantity(-1L);
            TEST_POSITION.setAcquisitionValue(TEST_POSITION.getAcquisitionPrice()
                    .multiply(BigDecimal.valueOf(TEST_POSITION.getQuantity())));
            TEST_POSITION.setTotalAcquisitionValue(TEST_POSITION.getAcquisitionValue());

            doReturn(Optional.of(TEST_POSITION)).when(positionRepository).findById(1L);

            assertThat(TEST_PORTFOLIO.getPositions()).hasSize(1);
            positionService.addDeal(TEST_DEAL);
            assertThat(TEST_PORTFOLIO.getPositions()).isEmpty();
        }
        @Test
        void addDealSellAndClosePosition() {
            TEST_DEAL.setDealType(DealType.SELL);
            TEST_DEAL.setQuantity(2L);
            TEST_DEAL.setAcquisitionValue(TEST_DEAL.getAcquisitionPrice()
                    .multiply(BigDecimal.valueOf(TEST_DEAL.getQuantity())));
            TEST_DEAL.setTotalAcquisitionValue(TEST_DEAL.getAcquisitionValue());

            doReturn(Optional.of(TEST_POSITION)).when(positionRepository).findById(1L);

            assertThat(TEST_PORTFOLIO.getPositions()).hasSize(1);
            positionService.addDeal(TEST_DEAL);
            assertThat(TEST_PORTFOLIO.getPositions()).isEmpty();
        }
        @Test
        void addDealFromLongToShort() {
            TEST_DEAL.setDealType(DealType.SELL);
            TEST_DEAL.setQuantity(3L);
            TEST_DEAL.setAcquisitionPrice(BigDecimal.valueOf(100));
            TEST_DEAL.setAcquisitionValue(TEST_DEAL.getAcquisitionPrice()
                    .multiply(BigDecimal.valueOf(TEST_DEAL.getQuantity())));
            TEST_DEAL.setTotalAcquisitionValue(TEST_DEAL.getAcquisitionValue());

            doReturn(Optional.of(TEST_POSITION)).when(positionRepository).findById(1L);

            positionService.addDeal(TEST_DEAL);
            assertThat(TEST_PORTFOLIO.getPositions()).hasSize(1);
            assertEquals(TEST_PORTFOLIO.getPositions().get(0).getPositionType(), PositionType.SHORT);
        }
        @Test
        void addDealFromShortToLong() {
            TEST_POSITION.setQuantity(-1L);
            TEST_POSITION.setPositionType(PositionType.SHORT);
            TEST_POSITION.setAcquisitionValue(TEST_POSITION.getAcquisitionPrice()
                    .multiply(BigDecimal.valueOf(TEST_POSITION.getQuantity())));
            TEST_POSITION.setTotalAcquisitionValue(TEST_POSITION.getAcquisitionValue());
            TEST_DEAL.setQuantity(2L);
            TEST_DEAL.setAcquisitionPrice(BigDecimal.valueOf(100));
            TEST_DEAL.setAcquisitionValue(TEST_DEAL.getAcquisitionPrice()
                    .multiply(BigDecimal.valueOf(TEST_DEAL.getQuantity())));
            TEST_DEAL.setTotalAcquisitionValue(TEST_DEAL.getAcquisitionValue());

            doReturn(Optional.of(TEST_POSITION)).when(positionRepository).findById(1L);

            positionService.addDeal(TEST_DEAL);
            assertThat(TEST_PORTFOLIO.getPositions()).hasSize(1);
            assertEquals(TEST_PORTFOLIO.getPositions().get(0).getPositionType(), PositionType.LONG);
        }
        @Test
        void updateFinancialResultWithProfit() {
            BigDecimal currentPrice = BigDecimal.valueOf(200);
            BigDecimal liquidationValue = currentPrice.multiply(BigDecimal.valueOf(TEST_POSITION.getQuantity()));
            BigDecimal profitLoss = liquidationValue.subtract(TEST_POSITION.getTotalAcquisitionValue());
            doReturn(currentPrice).when(priceService).getSharePriceByTicker(TEST_POSITION.getTicker());
            positionService.updateFinancialResult(TEST_POSITION);
            assertAll(() -> {
                assertEquals(currentPrice, TEST_POSITION.getCurrentPrice());
                assertEquals(liquidationValue, TEST_POSITION.getLiquidationValue());
                assertEquals(profitLoss, TEST_POSITION.getProfitLoss());
                assertEquals(100d, TEST_POSITION.getProfitLossPercentage());
            });
        }
        @Test
        void updateFinancialResultWithLoss() {
            BigDecimal currentPrice = BigDecimal.valueOf(50);
            BigDecimal liquidationValue = currentPrice.multiply(BigDecimal.valueOf(TEST_POSITION.getQuantity()));
            BigDecimal profitLoss = liquidationValue.subtract(TEST_POSITION.getTotalAcquisitionValue());
            doReturn(currentPrice).when(priceService).getSharePriceByTicker(TEST_POSITION.getTicker());
            positionService.updateFinancialResult(TEST_POSITION);
            assertAll(() -> {
                assertEquals(currentPrice, TEST_POSITION.getCurrentPrice());
                assertEquals(liquidationValue, TEST_POSITION.getLiquidationValue());
                assertEquals(profitLoss, TEST_POSITION.getProfitLoss());
                assertEquals(-50d, TEST_POSITION.getProfitLossPercentage());
            });
        }
        @Test
        void updateFinancialResultNoProfit() {
            BigDecimal currentPrice = BigDecimal.valueOf(100);
            BigDecimal liquidationValue = currentPrice.multiply(BigDecimal.valueOf(TEST_POSITION.getQuantity()));
            BigDecimal profitLoss = liquidationValue.subtract(TEST_POSITION.getTotalAcquisitionValue());
            doReturn(currentPrice).when(priceService).getSharePriceByTicker(TEST_POSITION.getTicker());
            positionService.updateFinancialResult(TEST_POSITION);
            assertAll(() -> {
                assertEquals(currentPrice, TEST_POSITION.getCurrentPrice());
                assertEquals(liquidationValue, TEST_POSITION.getLiquidationValue());
                assertEquals(profitLoss, TEST_POSITION.getProfitLoss());
                assertEquals(0d, TEST_POSITION.getProfitLossPercentage());
            });
        }
    }
}