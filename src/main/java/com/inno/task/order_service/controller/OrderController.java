package com.inno.task.order_service.controller;

import com.inno.task.order_service.dto.CreateOrderRequest;
import com.inno.task.order_service.dto.OrderResponse;
import com.inno.task.order_service.entity.OrderStatus;
import com.inno.task.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAll(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) List<OrderStatus> statuses,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrders(startDate, endDate, statuses, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.updateOrder(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}