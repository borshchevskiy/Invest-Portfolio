package ru.investportfolio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.investportfolio.controller.util.ControllerUtil;
import ru.investportfolio.database.entity.Deal;
import ru.investportfolio.dto.CashEditDTO;
import ru.investportfolio.dto.DealCreateDTO;
import ru.investportfolio.service.DealService;
import ru.investportfolio.service.PortfolioService;
import ru.investportfolio.service.PositionService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;
    private final PortfolioService portfolioService;
    private final PositionService positionService;
    private final MessageSource messageSource;

    @GetMapping("/portfolios/{id}/new-deal")
    public String newDeal(@PathVariable("id") Long portfolioId,
                          @RequestParam(value = "ticker", required = false) String ticker,
                          @RequestParam(value = "securityName", required = false) String securityName,
                          @RequestParam(value = "dealType", required = false) String dealType,
                          Model model) {

        model.addAttribute("portfolioId", portfolioId);
        model.addAttribute("securityNameAndTicker",
                (ticker == null || securityName == null) ? "" : securityName + " (" + ticker + ")");
        model.addAttribute("dealType", dealType);

        return "deals/new-deal";
    }

    @PostMapping("/portfolios/{id}/new-deal")
    public String addNewDeal(@ModelAttribute @Validated DealCreateDTO dealDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             @ModelAttribute("shareNames") ArrayList<String> shareNames,
                             @PathVariable("id") Long portfolioId,
                             @RequestParam(value = "hasCommission", required = false) String hasCommission,
                             HttpServletRequest request) {

        List<String> errors = new ArrayList<>();
        boolean contains = shareNames.contains(dealDTO.getSecurityNameAndTicker());

        if (bindingResult.hasErrors() || !contains) {
            if (!contains) {
                errors.add(messageSource.getMessage("controller.deal.add.security.not-found",
                        null,
                        ControllerUtil.getLocaleFromCookie(request)));
            }
            errors.addAll(ControllerUtil.gerErrorsMessages(bindingResult));
            redirectAttributes.addFlashAttribute("errors", errors);

            return "redirect:/portfolios/{id}/new-deal";
        }

        if (StringUtils.hasText(hasCommission)) {
            dealDTO.setCommission(true);
        }

        Optional<Deal> optionalDeal = dealService.create(dealDTO);

        if (optionalDeal.isEmpty()) {
            errors.add(messageSource.getMessage("controller.deal.add.cash.insufficient",
                    null,
                    ControllerUtil.getLocaleFromCookie(request)));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/portfolios/{id}/new-deal";
        }

        Deal deal = optionalDeal.get();
        positionService.addDeal(deal);
        CashEditDTO cashEditDTO = dealService.defineCashAmountInDeal(deal);
        portfolioService.updateCash(portfolioId, cashEditDTO);

        return "redirect:/portfolios/{id}";
    }
}
