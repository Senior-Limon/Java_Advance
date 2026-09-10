package com.inno.task.payment_service.controller;

import com.inno.task.payment_service.dto.CreatePaymentRequest;
import com.inno.task.payment_service.dto.PaymentResponse;
import com.inno.task.payment_service.entity.Payment;
import com.inno.task.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable String id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PaymentResponse>> searchPayments(
            @RequestParam(required = false) Long userId,
            Pageable pageable) {

        if (userId != null) {
            return ResponseEntity.ok(paymentService.getPaymentsByUser(userId, pageable));
        }
        return ResponseEntity.ok(Page.empty(pageable));
    }

    @GetMapping("/total/user")
    public ResponseEntity<BigDecimal> getUserTotalSum(
            @RequestParam Long userId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(paymentService.getTotalSpentByUser(userId, start, end));
    }

    @GetMapping("/total/revenue")
    public ResponseEntity<BigDecimal> getTotalRevenue(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(paymentService.getTotalRevenue(start, end));
    }
}