package com.inno.task.order_service.exception;

public class InvalidOrderStatusException extends RuntimeException {
    public InvalidOrderStatusException(String current, String target) {
        super("Cannot change status from " + current + " to " + target);
    }
}