package com.inno.task.payment_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotNull Long orderId,
        @NotNull Long userId,
        @NotNull @Positive BigDecimal paymentAmount
) {}