package ru.investportfolio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.investportfolio.controller.util.ControllerUtil;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.dto.UserEditDTO;
import ru.investportfolio.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MessageSource messageSource;

    @GetMapping()
    public String profile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "profile/profile";
    }

    @GetMapping("/update")
    public String getProfileUpdate(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "profile/updateProfile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute @Validated UserEditDTO userDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                @AuthenticationPrincipal User user) {

        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>(ControllerUtil.gerErrorsMessages(bindingResult));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/profile/update";
        }

        userService.update(user, userDTO);

        return "redirect:/profile";
    }

    @GetMapping("/update/password")
    public String getPasswordUpdate(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "profile/updatePassword";
    }

    @PostMapping("/update/password")
    public String updatePassword(@AuthenticationPrincipal User user,
                                 @RequestParam("password") String password,
                                 @RequestParam("passwordConfirm") String passwordConfirm,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {

        List<String> errors = new ArrayList<>();
        boolean isPasswordEmpty = !StringUtils.hasText(password);
        boolean isConfirmationEmpty = !StringUtils.hasText(passwordConfirm);

        if (isPasswordEmpty || isConfirmationEmpty) {
            errors.add(messageSource.getMessage("controller.user.update.password.empty",
                    null,
                    ControllerUtil.getLocaleFromCookie(request)));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/profile/update/password";
        }

        if (!password.equals(passwordConfirm)) {
            errors.add(messageSource.getMessage("controller.user.update.password.mismatch",
                    null,
                    ControllerUtil.getLocaleFromCookie(request)));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/profile/update/password";
        }

        userService.updatePassword(user, password);

        return "redirect:/profile";
    }


}
