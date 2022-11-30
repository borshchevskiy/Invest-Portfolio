package ru.investportfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.investportfolio.database.entity.*;
import ru.investportfolio.database.repository.DealRepository;
import ru.investportfolio.dto.CashEditDTO;
import ru.investportfolio.dto.DealCreateDTO;
import ru.investportfolio.dto.mapper.DealCreateMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DealService {

    private final DealRepository dealRepository;
    private final DealCreateMapper dealCreateMapper;
    private final PortfolioService portfolioService;
    private final PositionService positionService;
    private static final String CUSTOM_MARKET_COMMISSION = "custom";
    private static final BigDecimal MIN_MARKET_COMMISSION = BigDecimal.valueOf(0.02);

    @Transactional
    public Optional<Deal> create(DealCreateDTO dealDTO) {
        //Calculate total commission and acquisition value
        calculateAcquisitionValue(dealDTO);
        calculateCommissions(dealDTO);
        calculateTotalAcquisitionValue(dealDTO);

        //If BUY operation - check for sufficient money in portfolio
        Portfolio portfolio = portfolioService.findById(dealDTO.getPortfolioId());
        if (dealDTO.getDealType() == DealType.BUY) {
            if (portfolio.getCash().compareTo(dealDTO.getTotalAcquisitionValue()) < 0) {
                return Optional.empty();
            }
        }

        //Check if position for this security already exists. If there is no position - create new
        List<Position> positions = portfolio.getPositions();
        Position position = positions.stream()
                .filter(t -> t.getTicker().equals(dealDTO.getTicker()))
                .findFirst()
                .orElseGet(() -> positionService.createAndFlush(new Position(portfolio,
                        dealDTO.getSecurityName(),
                        dealDTO.getTicker())
                ));
        dealDTO.setPosition(position);
        Deal deal = dealCreateMapper.map(dealDTO);
        dealRepository.save(deal);
        return Optional.of(deal);
    }

    private void calculateCommissions(DealCreateDTO dealDTO) {
        if (dealDTO.isCommission()) {
            if (dealDTO.getBrokerCommission() == null) {
                dealDTO.setBrokerCommission(BigDecimal.ZERO);
            }
            if (dealDTO.getOtherCommission() == null) {
                dealDTO.setOtherCommission(BigDecimal.ZERO);
            }

            String marketCommissionType = dealDTO.getMarketCommissionType();

            /*
            If commission is not custom - calculate and set default market commission.
             Otherwise, check if user did not specify market commission. If so then set it to zero.
             */
            if (!CUSTOM_MARKET_COMMISSION.equals(marketCommissionType)) {
                BigDecimal marketCommission = dealDTO.getAcquisitionValue()
                        .multiply(BigDecimal.valueOf(0.0001))
                        .stripTrailingZeros();
                dealDTO.setMarketCommission(
                        marketCommission.compareTo(MIN_MARKET_COMMISSION) < 0 ?
                                MIN_MARKET_COMMISSION :
                                marketCommission);
            } else if (dealDTO.getMarketCommission() == null) {
                dealDTO.setMarketCommission(BigDecimal.ZERO);
            }
        } else {
            dealDTO.setMarketCommission(BigDecimal.ZERO);
            dealDTO.setBrokerCommission(BigDecimal.ZERO);
            dealDTO.setOtherCommission(BigDecimal.ZERO);
        }
    }

    private void calculateAcquisitionValue(DealCreateDTO dealDTO) {
        BigDecimal acqValue = dealDTO.getAcquisitionPrice()
                .multiply(BigDecimal.valueOf(dealDTO.getQuantity()));
        dealDTO.setAcquisitionValue(acqValue);
    }

    private void calculateTotalAcquisitionValue(DealCreateDTO dealDTO) {
        BigDecimal totalCommission = dealDTO.getMarketCommission()
                .add(dealDTO.getBrokerCommission())
                .add(dealDTO.getOtherCommission());
        dealDTO.setTotalAcquisitionValue(dealDTO.getAcquisitionValue()
                .add(totalCommission));
    }

    public CashEditDTO defineCashAmountInDeal(Deal deal) {
        CashEditDTO cashEditDTO = new CashEditDTO();

        if (deal.getDealType() == DealType.BUY) {
            cashEditDTO.setCash(deal.getTotalAcquisitionValue());
            cashEditDTO.setCashAction(CashAction.REMOVE);
        } else {
            cashEditDTO.setCash(deal.getTotalAcquisitionValue());
            cashEditDTO.setCashAction(CashAction.ADD);
        }
        return cashEditDTO;
    }
}
