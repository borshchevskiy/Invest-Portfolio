package ru.investportfolio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class EmptyShareDataException extends RuntimeException{

    public EmptyShareDataException() {
    }

    public EmptyShareDataException(String message) {
        super(message);
    }

    public EmptyShareDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
