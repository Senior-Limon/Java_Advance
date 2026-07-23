package com.inno.task.userservice.exception;

public class ValidationException extends RuntimeException{

    public ValidationException() {
        super();
    }
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ValidationException(Throwable throwable) {
        super(throwable);
    }
}
