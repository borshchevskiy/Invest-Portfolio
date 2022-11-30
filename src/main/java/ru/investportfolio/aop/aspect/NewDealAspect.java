package ru.investportfolio.aop.aspect;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.investportfolio.database.entity.Portfolio;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.exception.ActionNotAllowedException;
import ru.investportfolio.service.PortfolioService;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Aspect
@Component
@NoArgsConstructor
public class NewDealAspect {

    private PortfolioService portfolioService;

    @Before(value = "execution(* ru.investportfolio.controller.DealController.newDeal(..)) && args(portfolioId, ..)", argNames = "portfolioId")
    public void checkPortfolioOwner(Long portfolioId) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Portfolio> userPortfolios = portfolioService.findAllByUser(principal);
        boolean isOwner = userPortfolios.stream()
                .map(Portfolio::getId)
                .collect(toSet())
                .contains(portfolioId);
        if (!isOwner) {
            throw new ActionNotAllowedException("this action is not allowed");
        }
    }

    public PortfolioService getPortfolioService() {
        return portfolioService;
    }
    @Autowired
    public void setPortfolioService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }
}
