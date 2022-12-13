package ru.investportfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.investportfolio.database.entity.CashAction;
import ru.investportfolio.database.entity.Portfolio;
import ru.investportfolio.database.entity.Position;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.database.repository.PortfolioRepository;
import ru.investportfolio.dto.CashEditDTO;
import ru.investportfolio.exception.ItemNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private final PositionService positionService;
    private final PortfolioRepository portfolioRepository;

    public Set<Portfolio> findAllByUser(User user) {
        return portfolioRepository.findAllByUser(user)
                .orElse(new HashSet<>());
    }

    public Portfolio findById(Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Portfolio with id %d not found", id)));
    }

    public void updateFinancialResult(Portfolio portfolio) {
        updatePositionsResults(portfolio);
        sortPositions(portfolio);
        updatePortfolioValues(portfolio);

        BigDecimal acquisitionValue = portfolio.getPositions().stream()
                .map(Position::getAcquisitionValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (portfolio.getProfitLoss().equals(BigDecimal.ZERO)) {
            portfolio.setProfitLossPercentage(0d);
        } else {
            Double profitLossPercentage = Double.valueOf(
                    portfolio.getProfitLoss()
                            .divide(acquisitionValue, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .toPlainString());
            portfolio.setProfitLossPercentage(profitLossPercentage);
        }
    }

    private void updatePositionsResults(Portfolio portfolio) {
        portfolio.getPositions().forEach(positionService::updateFinancialResult);
    }

    private void sortPositions(Portfolio portfolio) {
        portfolio.getPositions().sort(Comparator.comparing(Position::getSecurityName));
    }

    private void updatePortfolioValues(Portfolio portfolio) {
        BigDecimal positionsValue = portfolio.getPositions().stream()
                .map(Position::getLiquidationValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal acquisitionValue = portfolio.getPositions().stream()
                .map(Position::getAcquisitionValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profitLoss = positionsValue.subtract(acquisitionValue);

        portfolio.setPositionsValue(positionsValue);
        portfolio.setTotalValue(portfolio.getPositionsValue().add(portfolio.getCash()));
        portfolio.setProfitLoss(profitLoss);
    }

    @Transactional
    public Portfolio create(String name) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return portfolioRepository.save(new Portfolio(name, user));
    }

    @Transactional
    public Portfolio update(String name, Long id) {
        Optional<Portfolio> portfolio = portfolioRepository.findById(id);
        return portfolio.map(p -> {
                    p.setName(name);
                    portfolioRepository.saveAndFlush(p);
                    return p;
                })
                .orElseThrow(() -> new ItemNotFoundException(String.format("Portfolio with id %d not found", id)));
    }

    @Transactional
    public boolean delete(Long id) {
        return portfolioRepository.findById(id).map(p -> {
            portfolioRepository.delete(p);
            portfolioRepository.flush();
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean updateCash(Long id, CashEditDTO cashEditDTO) {
        Portfolio portfolio = findById(id);
        if (cashEditDTO.getCashAction() == CashAction.REMOVE) {
            if (portfolio.getCash().compareTo(cashEditDTO.getCash()) < 0) {
                return false;
            }
            removeCash(portfolio, cashEditDTO.getCash());
        } else {
            addCash(portfolio, cashEditDTO.getCash());
        }
        return true;
    }

    private void removeCash(Portfolio portfolio, BigDecimal cash) {
        portfolio.setCash(portfolio.getCash().subtract(cash));
        portfolioRepository.save(portfolio);
    }

    private void addCash(Portfolio portfolio, BigDecimal cash) {
        portfolio.setCash(portfolio.getCash().add(cash));
        portfolioRepository.save(portfolio);
    }
}
