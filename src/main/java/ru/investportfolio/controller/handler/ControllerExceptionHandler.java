package ru.investportfolio.controller.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.investportfolio.exception.ActionNotAllowedException;
import ru.investportfolio.exception.ItemNotFoundException;

@Slf4j
@ControllerAdvice(basePackages = "ru.investportfolio.controller")
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ItemNotFoundException.class)
    public String handleNotFoundException(ItemNotFoundException exception,
                                          Model model){
        log.error("Failed to return response", exception);
        model.addAttribute("message", exception.getMessage());
        return "error/error404";
    }

    @ExceptionHandler(ActionNotAllowedException.class)
    public String handleActionNotAllowedException(ActionNotAllowedException exception,
                                                  Model model){
        log.error("Failed to return response", exception);
        model.addAttribute("message", exception.getMessage());
        return "error/error403";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception exception){
        log.error("Failed to return response", exception);
        return "error/error500";
    }
}
