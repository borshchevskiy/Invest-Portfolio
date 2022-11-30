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
import ru.investportfolio.database.entity.User;
import ru.investportfolio.dto.UserEditDTO;
import ru.investportfolio.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public String profile(@AuthenticationPrincipal User user,
                          Model model) {
        model.addAttribute("user", user);
        return "/profile/profile";
    }

    @GetMapping("/update")
    public String getProfileUpdate(@AuthenticationPrincipal User user,
                                   Model model) {
        model.addAttribute("user", user);
        return "/profile/updateProfile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute @Validated UserEditDTO userDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                @AuthenticationPrincipal User user) {
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>(ControllerUtils.gerErrorsList(bindingResult));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/profile/update";
        }
        userService.update(user, userDTO);
        return "redirect:/profile";
    }

    @GetMapping("/update/password")
    public String getPasswordUpdate(@AuthenticationPrincipal User user,
                                    Model model) {
        model.addAttribute("user", user);
        return "/profile/updatePassword";
    }

    @PostMapping("/update/password")
    public String updatePassword(@AuthenticationPrincipal User user,
                                 @RequestParam("password") String password,
                                 @RequestParam("passwordConfirm") String passwordConfirm,
                                 RedirectAttributes redirectAttributes) {
        List<String> errors = new ArrayList<>();
        boolean isPasswordEmpty = !StringUtils.hasText(password);
        boolean isConfirmationEmpty = !StringUtils.hasText(passwordConfirm);

        if (isPasswordEmpty || isConfirmationEmpty) {
            errors.add("Both Password and Confirmation should be filled!");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/profile/update/password";
        }

        if (!password.equals(passwordConfirm)) {
            errors.add("Password and Confirmation must match!");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/profile/update/password";
        }

        userService.updatePassword(user, password);
        return "redirect:/profile";
    }


}
