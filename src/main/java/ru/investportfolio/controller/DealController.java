package ru.investportfolio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.investportfolio.controller.utils.ControllerUtils;
import ru.investportfolio.database.entity.Deal;
import ru.investportfolio.dto.CashEditDTO;
import ru.investportfolio.dto.DealCreateDTO;
import ru.investportfolio.dto.ShareDatalistDTO;
import ru.investportfolio.service.DealService;
import ru.investportfolio.service.PortfolioService;
import ru.investportfolio.service.PositionService;
import ru.investportfolio.service.ShareService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.naturalOrder;

@Controller
@ControllerAdvice(basePackages = "ru.investportfolio.controller.DealController")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;
    private final PortfolioService portfolioService;
    private final PositionService positionService;
    private final ShareService shareService;

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
        return "/deals/new-deal";
    }

    @PostMapping("/portfolios/{id}/new-deal")
    public String addNewDeal(@ModelAttribute @Validated DealCreateDTO dealDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             @ModelAttribute("names") ArrayList<String> names,
                             @PathVariable("id") Long portfolioId,
                             @RequestParam(value = "hasCommission", required = false) String hasCommission) {

        List<String> errors = new ArrayList<>();
        boolean contains = names.contains(dealDTO.getSecurityNameAndTicker());

        if (bindingResult.hasErrors() || !contains) {
            if (!contains) {
                errors.add("Can't find security with specified name. Please, choose from suggested list!");
            }
            errors.addAll(ControllerUtils.gerErrorsList(bindingResult));
            redirectAttributes.addFlashAttribute("errors", errors);

            return "redirect:/portfolios/{id}/new-deal";
        }
        if (StringUtils.hasText(hasCommission)) {
            dealDTO.setCommission(true);
        }

        Optional<Deal> optionalDeal = dealService.create(dealDTO);

        if (optionalDeal.isEmpty()) {
            errors.add("Can't create this deal. Please check if you have sufficient money in portfolio");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/portfolios/{id}/new-deal";
        }

        Deal deal = optionalDeal.get();
        positionService.addDeal(deal);
        CashEditDTO cashEditDTO = dealService.defineCashAmountInDeal(deal);
        portfolioService.updateCash(portfolioId, cashEditDTO);
        return "redirect:/portfolios/{id}";
    }

    @ModelAttribute("names")
    public List<String> getShareNamesAndTickers() {
        List<String> names = new ArrayList<>(shareService.getShareDatalist()
                .stream()
                .map(ShareDatalistDTO::toString)
                .toList());
        names.sort(naturalOrder());
        return names;
    }
}
