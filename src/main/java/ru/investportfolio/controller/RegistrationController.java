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
import org.springframework.web.util.WebUtils;
import ru.investportfolio.controller.util.ControllerUtil;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.dto.UserCreateDTO;
import ru.investportfolio.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final MessageSource messageSource;

    @GetMapping
    public String getRegistration(UserCreateDTO user, Model model) {
        model.addAttribute("user", user);
        return "registration";
    }

    @PostMapping
    public String register(@ModelAttribute @Validated UserCreateDTO user,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           @RequestParam(value = "passwordConfirm", required = false) String passwordConfirm,
                           HttpServletRequest request) {

        List<String> errors = new ArrayList<>();

        if (!StringUtils.hasText(passwordConfirm)) {
//            errors.add("Password confirmation can't be empty!");
            errors.add(messageSource.getMessage("controller.registration.register.confirmation.empty",
                    null,
                    ControllerUtil.getLocaleFromCookie(request)));
        }
        if (user.getPassword() != null && !user.getPassword().equals(passwordConfirm)) {
            errors.add(messageSource.getMessage("controller.registration.register.confirmation.different",
                    null,
                    ControllerUtil.getLocaleFromCookie(request)));
        }
        if (!errors.isEmpty() || bindingResult.hasErrors()) {
            errors.addAll(ControllerUtil.gerErrorsMessages(bindingResult));
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/registration";
        }
        if (!userService.create(user)) {
            errors.add(messageSource.getMessage("controller.registration.register.email.exists",
                    null,
                    ControllerUtil.getLocaleFromCookie(request)));
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/registration";
        }

        return "redirect:/login";
    }
}
