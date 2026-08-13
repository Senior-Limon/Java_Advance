package com.inno.task.order_service.exception;

public class OrderAlreadyDeletedException extends RuntimeException {
    public OrderAlreadyDeletedException(Long id) {
        super("Order with id " + id + " is already deleted");
    }
}