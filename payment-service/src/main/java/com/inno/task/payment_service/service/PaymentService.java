package com.inno.task.payment_service.service;

import com.inno.task.payment_service.dto.CreatePaymentRequest;
import com.inno.task.payment_service.dto.PaymentEvent;
import com.inno.task.payment_service.dto.PaymentResponse;
import com.inno.task.payment_service.entity.Payment;
import com.inno.task.payment_service.entity.PaymentStatus;
import com.inno.task.payment_service.exception.PaymentNotFoundException;
import com.inno.task.payment_service.kafka.PaymentKafkaProducer;
import com.inno.task.payment_service.mapper.PaymentMapper;
import com.inno.task.payment_service.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper mapper;
    private final PaymentKafkaProducer paymentKafkaProducer;

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Payment payment = mapper.toEntity(request);
        payment.setStatus(simulatePaymentResult());
        payment.setTimestamp(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);
        
        PaymentEvent event = PaymentEvent.builder()
                .paymentId(saved.getId())
                .orderId(saved.getOrderId())
                .userId(saved.getUserId())
                .amount(saved.getPaymentAmount())
                .status(saved.getStatus().name())
                .timestamp(saved.getTimestamp())
                .build();

        paymentKafkaProducer.sendPaymentCreated(event);
        log.info("Create new payment with id: {}", saved.getId());
        return mapper.toResponse(saved);
    }

    public BigDecimal getTotalSpentByUser(Long userId, LocalDateTime start, LocalDateTime end) {
        if (userId == null || start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid input parameters: userId, start and end must not be null, and start must be before end");
        }
        BigDecimal result = paymentRepository.getTotalSumForAllUsersByDateRange(start, end);
        return result != null ? result : BigDecimal.ZERO;
    }

    public BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid date, start must be before end");
        }
        BigDecimal result = paymentRepository.getTotalSumForAllUsersByDateRange(start, end);
        return result != null ? result : BigDecimal.ZERO;
    }

    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("payment with id: " + paymentId + " not found"));
    }

    public Page<PaymentResponse> getPaymentsByUser(Long userId, Pageable pageable) {
        Page<Payment> paymentsPage = paymentRepository.findByUserId(userId, pageable);
        return paymentsPage.map(mapper::toResponse);
    }

//randomizer
    private PaymentStatus simulatePaymentResult() {
        boolean isSuccess = ThreadLocalRandom.current().nextInt(100) % 2 == 0;
        return isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}
