package ru.investportfolio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ActionNotAllowedException extends RuntimeException{
    public ActionNotAllowedException() {
    }
    public ActionNotAllowedException(String message) {
        super(message);
    }
    public ActionNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
