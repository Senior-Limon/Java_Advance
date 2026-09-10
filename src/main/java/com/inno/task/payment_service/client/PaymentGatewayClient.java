package com.inno.task.payment_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Placeholder for external payment gateway integration.
 * In a production environment, this client would use RestClient or WebClient
 * to communicate with an actual payment provider (e.g., Stripe, PayPal).
 * Currently, it simulates successful processing without making real HTTP calls.
 */
@Component
@Slf4j
public class PaymentGatewayClient {

    /**
     * Simulates sending a payment request to an external gateway.
     *
     * @param orderId   the order identifier
     * @param amount    the payment amount
     * @return true if the simulated gateway accepts the payment
     */
    public boolean processPayment(Long orderId, java.math.BigDecimal amount) {
        log.debug("Simulating payment gateway call for order {} with amount {}", orderId, amount);
        // In production: restTemplate.postForObject(gatewayUrl, request, Response.class);
        return true;
    }
}