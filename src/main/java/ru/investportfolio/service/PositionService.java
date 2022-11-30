package ru.investportfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.investportfolio.database.entity.*;
import ru.investportfolio.database.repository.PositionRepository;
import ru.investportfolio.exception.ItemNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PositionService {

    private final PositionRepository positionRepository;
    private final PriceService priceService;

    public Position findById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Position with id %d not found", id)));
    }

    @Transactional
    public Position create(Position position) {
        return positionRepository.save(position);
    }

    @Transactional
    public Position createAndFlush(Position position) {
        return positionRepository.saveAndFlush(position);
    }

    @Transactional
    public boolean delete(Long positionId) {
        return positionRepository.findById(positionId)
                .map(entity -> {
                    entity.getPortfolio().getPositions().remove(entity);
                    positionRepository.delete(entity);
                    return true;
                }).orElse(false);
    }

    @Transactional
    public void addDeal(Deal deal) {
        Position position = findById(deal.getPosition().getId());
        if (deal.getDealType() == DealType.BUY) {
            position.setQuantity(position.getQuantity() + deal.getQuantity());
            position.setAcquisitionValue(
                    position.getAcquisitionValue()
                            .add(deal.getAcquisitionValue()));
            position.setTotalAcquisitionValue(
                    position.getTotalAcquisitionValue()
                            .add(deal.getTotalAcquisitionValue()));
        }
        if (deal.getDealType() == DealType.SELL) {
            position.setQuantity(position.getQuantity() - deal.getQuantity());
            position.setAcquisitionValue(
                    position.getAcquisitionValue()
                            .subtract(deal.getAcquisitionValue()));
            position.setTotalAcquisitionValue(
                    position.getTotalAcquisitionValue()
                            .subtract(deal.getTotalAcquisitionValue()));
        }
        if (position.getQuantity().equals(0L)) {
            delete(position.getId());
            return;
        }

        position.setPositionType(position.getQuantity() > 0 ? PositionType.LONG : PositionType.SHORT);

        position.setAcquisitionPrice(
                position.getAcquisitionValue()
                        .divide(BigDecimal.valueOf(position.getQuantity()), RoundingMode.HALF_UP));
        positionRepository.save(position);
    }

    public void updateFinancialResult(Position position) {
        BigDecimal currentPrice = priceService.getSharePriceByTicker(position.getTicker());
        position.setCurrentPrice(currentPrice);
        BigDecimal liquidationValue = position.getCurrentPrice()
                .multiply(BigDecimal.valueOf(position.getQuantity()));
        position.setLiquidationValue(liquidationValue);
        BigDecimal profitLoss = position.getLiquidationValue()
                .subtract(position.getTotalAcquisitionValue());
        position.setProfitLoss(profitLoss);

        if (profitLoss.equals(BigDecimal.ZERO)) {
            position.setProfitLossPercentage(0d);
        } else {
            Double profitLossPercentage = Double.valueOf(
                    position.getProfitLoss()
                            .divide(position.getTotalAcquisitionValue(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .toPlainString());
            position.setProfitLossPercentage(profitLossPercentage);
        }
    }

}
