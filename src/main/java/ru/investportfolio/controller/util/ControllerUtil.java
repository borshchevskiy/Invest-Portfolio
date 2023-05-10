package ru.investportfolio.controller.util;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class ControllerUtil {
    public static List<String> gerErrorsMessages(BindingResult bindingResult) {
        return bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
    }

    public static Locale getLocaleFromCookie(HttpServletRequest request) {
        Cookie langCookie = WebUtils.getCookie(request, "lang");
        if (langCookie == null || !StringUtils.hasText(langCookie.getValue())) {
            return Locale.getDefault();
        }
        return new Locale(langCookie.getValue());
    }
}
