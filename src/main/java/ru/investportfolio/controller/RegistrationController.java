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
import ru.investportfolio.database.entity.User;
import ru.investportfolio.dto.UserCreateDTO;
import ru.investportfolio.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping
    public String getRegistration(User user,
                                  Model model) {
        model.addAttribute("user", user);
        return "registration";
    }

    @PostMapping
    public String register(@ModelAttribute @Validated UserCreateDTO user,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           @RequestParam("passwordConfirm") String passwordConfirm) {

        boolean isConfirmationEmpty = !StringUtils.hasText(passwordConfirm);
        List<String> errors = new ArrayList<>();

        if (isConfirmationEmpty) {
            errors.add("Password confirmation can't be empty!");
        }
        if (user.getPassword() != null && !user.getPassword().equals(passwordConfirm)) {
            errors.add("Passwords are different!");
        }
        if (isConfirmationEmpty || bindingResult.hasErrors()) {
            errors.addAll(ControllerUtils.gerErrorsList(bindingResult));
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/registration";
        }

        if (!userService.create(user)){
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("errors", "User already exists!");
            return "redirect:/registration";
        }
        return "redirect:/login";
    }
}
