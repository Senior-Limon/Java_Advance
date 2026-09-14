package com.inno.task.order_service.kafka;

import com.inno.task.order_service.dto.PaymentEvent;
import com.inno.task.order_service.entity.Order;
import com.inno.task.order_service.entity.OrderStatus;
import com.inno.task.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentKafkaConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment-events", groupId = "order-service-group")
    @Transactional
    public void consumePaymentEvent(@Payload PaymentEvent event) {
        log.info("Received payment event for order {}: status={}", event.getOrderId(), event.getStatus());

        orderRepository.findById(event.getOrderId())
                .ifPresentOrElse(order -> {
                    if ("SUCCESS".equalsIgnoreCase(event.getStatus())) {
                        order.setStatus(OrderStatus.PAID);
                        log.info("Order {} marked as PAID", event.getOrderId());
                    } else if ("FAILED".equalsIgnoreCase(event.getStatus())) {
                        order.setStatus(OrderStatus.PAYMENT_FAILED);
                        log.warn("Order {} marked as PAYMENT_FAILED", event.getOrderId());
                    }
                    orderRepository.save(order);
                }, () -> log.error("Order not found: {}", event.getOrderId()));
    }
}