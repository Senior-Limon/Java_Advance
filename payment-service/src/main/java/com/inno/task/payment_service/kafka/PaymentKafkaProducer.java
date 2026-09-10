package com.inno.task.payment_service.kafka;

import com.inno.task.payment_service.dto.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentKafkaProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private static final String TOPIC = "payment-events";

    public void sendPaymentCreated(PaymentEvent event) {
        kafkaTemplate.send(TOPIC, String.valueOf(event.getOrderId()), event);
    }
}