package com.inno.task.payment_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        String id,
        Long orderId,
        Long userId,
        String status,
        LocalDateTime timestamp,
        BigDecimal paymentAmount
) {}