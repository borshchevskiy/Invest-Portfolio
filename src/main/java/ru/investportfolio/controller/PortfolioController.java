package ru.investportfolio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.investportfolio.controller.utils.ControllerUtils;
import ru.investportfolio.database.entity.*;
import ru.investportfolio.dto.CashEditDTO;
import ru.investportfolio.service.PortfolioService;
import ru.investportfolio.service.PositionService;

import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final PositionService positionService;

    @GetMapping
    public String getUserPortfolios(@AuthenticationPrincipal User user,
                                    Model model) {
        Set<Portfolio> portfolios = portfolioService.findAllByUser(user);
        portfolios.forEach(portfolioService::updateFinancialResult);
        model.addAttribute("portfolios", portfolios);
        return "portfolios/portfolios";
    }

    @GetMapping("/{id}")
    public String getUserPortfolio(@PathVariable Long id,
                                   @AuthenticationPrincipal User user,
                                   Model model) {
        Portfolio portfolio = portfolioService.findById(id);
        portfolioService.updateFinancialResult(portfolio);
        model.addAttribute("portfolio", portfolio);
        model.addAttribute("userId", user.getId());
        return "portfolios/portfolio";
    }

    @PostMapping("/create")
    public String createPortfolio(@RequestParam("name") String name,
                                  @ModelAttribute @Validated CashEditDTO cash,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(name)) {
            redirectAttributes.addFlashAttribute("createErrors", "Name can't be empty");
            return "redirect:/portfolios";
        }
        Portfolio portfolio = portfolioService.create(name);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("cashErrors", ControllerUtils.gerErrorsList(bindingResult));
            return "redirect:/portfolios";
        }
        portfolioService.updateCash(portfolio.getId(), cash);
        return "redirect:/portfolios";
    }

    @PostMapping("/{id}/update")
    public String updatePortfolio(@PathVariable Long id,
                                  @RequestParam("name") String name,
                                  RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(name)) {
            redirectAttributes.addFlashAttribute("updateErrors", "Name can't be empty");
            return "redirect:/portfolios/{id}";
        }
        portfolioService.update(name, id);
        return "redirect:/portfolios/{id}";
    }

    @PostMapping("/{id}/delete")
    public String deletePortfolio(@PathVariable Long id,
                                  @RequestParam("name") String name,
                                  RedirectAttributes redirectAttributes) {
        boolean isDeleted = portfolioService.delete(id);
        redirectAttributes.addFlashAttribute("deletedPortfolioName", name);
        redirectAttributes.addFlashAttribute("isDeleted", isDeleted);
        if (isDeleted) {
            return "redirect:/portfolios";
        }
        return "redirect:/portfolios/{id}";
    }

    @PostMapping("/{id}/cash")
    public String changeCash(@ModelAttribute @Validated CashEditDTO cash,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             @PathVariable Long id) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("cashErrors", ControllerUtils.gerErrorsList(bindingResult));
            return "redirect:/portfolios/{id}";
        }

        boolean isUpdated = portfolioService.updateCash(id, cash);

        if (!isUpdated) {
            redirectAttributes.addFlashAttribute("cashErrors",
                    "Cash balance can't become negative!");
        }
        return "redirect:/portfolios/{id}";
    }

    @PostMapping("/{id}/delete-position")
    public String deletePosition(@RequestParam("positionId") Long positionId) {
        positionService.delete(positionId);
        return "redirect:/portfolios/{id}";
    }
}
