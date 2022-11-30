package ru.investportfolio.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.investportfolio.database.entity.*;
import ru.investportfolio.database.repository.DealRepository;
import ru.investportfolio.dto.DealCreateDTO;
import ru.investportfolio.dto.mapper.DealCreateMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    private DealRepository dealRepository;
    @Mock
    private DealCreateMapper dealCreateMapper;
    @Mock
    private PortfolioService portfolioService;
    @Mock
    private PositionService positionService;
    private static final String CUSTOM_MARKET_COMMISSION = "custom";
    private static final BigDecimal MIN_MARKET_COMMISSION = BigDecimal.valueOf(0.02);
    private static final Deal TEST_DEAL = new Deal();
    private static final DealCreateDTO TEST_DEAL_DTO = new DealCreateDTO();
    private static final Portfolio TEST_PORTFOLIO = new Portfolio();
    private static final Position TEST_POSITION_1 = new Position();
    private static final Position TEST_POSITION_2 = new Position();

    @InjectMocks
    private DealService dealService;

    @BeforeAll
    static void prepare() {
        TEST_PORTFOLIO.setPositionsValue(BigDecimal.valueOf(200));
        TEST_PORTFOLIO.setPositions(new ArrayList<>(List.of(TEST_POSITION_1, TEST_POSITION_2)));
        TEST_PORTFOLIO.setCash(BigDecimal.valueOf(1000));
        TEST_POSITION_1.setTicker("POS1");
        TEST_POSITION_1.setPortfolio(TEST_PORTFOLIO);
        TEST_POSITION_2.setTicker("POS2");
        TEST_POSITION_2.setPortfolio(TEST_PORTFOLIO);
        TEST_DEAL.setDealType(DealType.BUY);
        TEST_DEAL.setTotalAcquisitionValue(BigDecimal.valueOf(200));
        TEST_DEAL_DTO.setAcquisitionPrice(BigDecimal.valueOf(100));
        TEST_DEAL_DTO.setSecurityNameAndTicker("Position1 (POS1)");
        TEST_DEAL_DTO.setQuantity(2L);
        TEST_DEAL_DTO.setCommission(false);
        TEST_DEAL_DTO.setPortfolioId(1L);
        TEST_DEAL_DTO.setDealType(DealType.BUY);
    }

    @AfterEach
    void afterEach() {
        TEST_PORTFOLIO.setCash(BigDecimal.valueOf(1000));
        TEST_DEAL_DTO.setSecurityNameAndTicker("Position1 (POS1)");
        TEST_DEAL_DTO.setQuantity(2L);
        TEST_DEAL_DTO.setAcquisitionPrice(BigDecimal.valueOf(100));
        TEST_DEAL_DTO.setCommission(false);
        TEST_DEAL_DTO.setMarketCommission(BigDecimal.ZERO);
        TEST_DEAL_DTO.setBrokerCommission(BigDecimal.ZERO);
        TEST_DEAL_DTO.setOtherCommission(BigDecimal.ZERO);
        TEST_DEAL_DTO.setPosition(null);
        TEST_DEAL.setDealType(DealType.BUY);
        TEST_DEAL.setTotalAcquisitionValue(BigDecimal.valueOf(200));
        TEST_DEAL.setSecurityName(null);
        TEST_DEAL.setTicker(null);
    }

    @Nested
    public class PrivateMethods {
        @Nested
        public class Commissions {
            @Test
            void noCommissions() {
                doReturn(TEST_PORTFOLIO).when(portfolioService).findById(1L);
                doReturn(TEST_DEAL).when(dealCreateMapper).map(TEST_DEAL_DTO);
                doReturn(TEST_DEAL).when(dealRepository).save(TEST_DEAL);
                var actualDeal = dealService.create(TEST_DEAL_DTO);
                assertAll(() -> {
                    assertEquals(BigDecimal.ZERO, TEST_DEAL_DTO.getMarketCommission());
                    assertEquals(BigDecimal.ZERO, TEST_DEAL_DTO.getBrokerCommission());
                    assertEquals(BigDecimal.ZERO, TEST_DEAL_DTO.getOtherCommission());
                });
            }
            @Test
            void defaultMarketCommission() {
                TEST_DEAL_DTO.setCommission(true);
                TEST_DEAL_DTO.setMarketCommissionType("default");
                doReturn(TEST_PORTFOLIO).when(portfolioService).findById(1L);
                doReturn(TEST_DEAL).when(dealCreateMapper).map(TEST_DEAL_DTO);
                doReturn(TEST_DEAL).when(dealRepository).save(TEST_DEAL);
                var actualDeal = dealService.create(TEST_DEAL_DTO);

                assertAll(() -> {
                    assertEquals(BigDecimal.valueOf(0.02), TEST_DEAL_DTO.getMarketCommission());
                    assertEquals(BigDecimal.ZERO, TEST_DEAL_DTO.getBrokerCommission());
                    assertEquals(BigDecimal.ZERO, TEST_DEAL_DTO.getOtherCommission());
                });
            }
            @Test
            void customMarketCommission() {
                TEST_DEAL_DTO.setCommission(true);
                TEST_DEAL_DTO.setMarketCommissionType("custom");
                BigDecimal marketCommission = BigDecimal.valueOf(0.05);
                TEST_DEAL_DTO.setMarketCommission(marketCommission);
                doReturn(TEST_PORTFOLIO).when(portfolioService).findById(1L);
                doReturn(TEST_DEAL).when(dealCreateMapper).map(TEST_DEAL_DTO);
                doReturn(TEST_DEAL).when(dealRepository).save(TEST_DEAL);
                var actualDeal = dealService.create(TEST_DEAL_DTO);

                assertAll(() -> {
                    assertEquals(marketCommission, TEST_DEAL_DTO.getMarketCommission());
                    assertEquals(BigDecimal.ZERO, TEST_DEAL_DTO.getBrokerCommission());
                    assertEquals(BigDecimal.ZERO, TEST_DEAL_DTO.getOtherCommission());
                });
            }
            @Test
            void customCommissions() {
                TEST_DEAL_DTO.setCommission(true);
                TEST_DEAL_DTO.setMarketCommissionType("custom");
                BigDecimal marketCommission = BigDecimal.valueOf(0.05);
                BigDecimal brokerCommission = BigDecimal.valueOf(0.06);
                BigDecimal otherCommission = BigDecimal.valueOf(0.07);
                TEST_DEAL_DTO.setMarketCommission(marketCommission);
                TEST_DEAL_DTO.setBrokerCommission(brokerCommission);
                TEST_DEAL_DTO.setOtherCommission(otherCommission);
                doReturn(TEST_PORTFOLIO).when(portfolioService).findById(1L);
                doReturn(TEST_DEAL).when(dealCreateMapper).map(TEST_DEAL_DTO);
                doReturn(TEST_DEAL).when(dealRepository).save(TEST_DEAL);
                var actualDeal = dealService.create(TEST_DEAL_DTO);

                assertAll(() -> {
                    assertEquals(marketCommission, TEST_DEAL_DTO.getMarketCommission());
                    assertEquals(brokerCommission, TEST_DEAL_DTO.getBrokerCommission());
                    assertEquals(otherCommission, TEST_DEAL_DTO.getOtherCommission());
                });
            }
        }

        @Test
        @DisplayName("Acquisition and TotalAcquisition values")
        void acquisitionValues() {
            TEST_DEAL_DTO.setCommission(true);
            TEST_DEAL_DTO.setMarketCommissionType("custom");
            BigDecimal marketCommission = BigDecimal.valueOf(0.05);
            BigDecimal brokerCommission = BigDecimal.valueOf(0.06);
            BigDecimal otherCommission = BigDecimal.valueOf(0.07);
            TEST_DEAL_DTO.setMarketCommission(marketCommission);
            TEST_DEAL_DTO.setBrokerCommission(brokerCommission);
            TEST_DEAL_DTO.setOtherCommission(otherCommission);
            doReturn(TEST_PORTFOLIO).when(portfolioService).findById(1L);
            doReturn(TEST_DEAL).when(dealCreateMapper).map(TEST_DEAL_DTO);
            doReturn(TEST_DEAL).when(dealRepository).save(TEST_DEAL);
            var actualDeal = dealService.create(TEST_DEAL_DTO);

            assertAll(() -> {
                assertEquals(BigDecimal.valueOf(200), TEST_DEAL_DTO.getAcquisitionValue());
                assertEquals(BigDecimal.valueOf(200.18), TEST_DEAL_DTO.getTotalAcquisitionValue());
            });
        }
    }

    @Test
    void createDealNotEnoughCash() {
        doReturn(TEST_PORTFOLIO).when(portfolioService).findById(1L);
        TEST_DEAL_DTO.setQuantity(200L);
        var actualDeal = dealService.create(TEST_DEAL_DTO);

        assertTrue(actualDeal.isEmpty());
    }
    @Test
    void createDealWithExistingPosition() {
        TEST_DEAL.setSecurityName(TEST_DEAL_DTO.getSecurityName());
        TEST_DEAL.setTicker(TEST_DEAL_DTO.getTicker());
        doReturn(TEST_PORTFOLIO).when(portfolioService).findById(1L);
        doReturn(TEST_DEAL).when(dealCreateMapper).map(TEST_DEAL_DTO);
        doReturn(TEST_DEAL).when(dealRepository).save(TEST_DEAL);
        var actualDeal = dealService.create(TEST_DEAL_DTO);

        assertAll(() -> {
            assertEquals(TEST_POSITION_1, TEST_DEAL_DTO.getPosition());
            assertTrue(actualDeal.isPresent());
            assertEquals(TEST_DEAL, actualDeal.get());
        });
    }
    @Test
    void createDealNewPosition() {
        TEST_DEAL_DTO.setSecurityNameAndTicker("Position3 (POS3)");
        Position testPosition = new Position(TEST_PORTFOLIO,
                TEST_DEAL_DTO.getSecurityName(),
                TEST_DEAL_DTO.getTicker());
        doReturn(TEST_PORTFOLIO).when(portfolioService).findById(1L);
        doReturn(testPosition).when(positionService).createAndFlush(any(Position.class));
        doReturn(TEST_DEAL).when(dealCreateMapper).map(TEST_DEAL_DTO);
        doReturn(TEST_DEAL).when(dealRepository).save(TEST_DEAL);
        var actualDeal = dealService.create(TEST_DEAL_DTO);

        assertAll(() -> {
            assertEquals(testPosition, TEST_DEAL_DTO.getPosition());
            assertTrue(actualDeal.isPresent());
            assertEquals(TEST_DEAL, actualDeal.get());
        });
    }

    @Test
    void defineCashAmountInBuyDeal() {
        var cashEditDTO = dealService.defineCashAmountInDeal(TEST_DEAL);
        assertAll(() -> {
            assertEquals(BigDecimal.valueOf(200), cashEditDTO.getCash());
            assertEquals(CashAction.REMOVE, cashEditDTO.getCashAction());
        });

    }
    @Test
    void defineCashAmountInSellDeal() {
        TEST_DEAL.setDealType(DealType.SELL);
        var cashEditDTO = dealService.defineCashAmountInDeal(TEST_DEAL);
        assertAll(() -> {
            assertEquals(BigDecimal.valueOf(200), cashEditDTO.getCash());
            assertEquals(CashAction.ADD, cashEditDTO.getCashAction());
        });
    }
}