package ru.investportfolio.exception;

public class EmptyShareDataException extends RuntimeException {

    public EmptyShareDataException() {
    }

    public EmptyShareDataException(String message) {
        super(message);
    }

    public EmptyShareDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
