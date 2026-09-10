package com.inoo.task.payment_service.service;

import com.inno.task.payment_service.dto.CreatePaymentRequest;
import com.inno.task.payment_service.dto.PaymentResponse;
import com.inno.task.payment_service.entity.Payment;
import com.inno.task.payment_service.entity.PaymentStatus;
import com.inno.task.payment_service.mapper.PaymentMapper;
import com.inno.task.payment_service.repository.PaymentRepository;
import com.inno.task.payment_service.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper mapper;

    @InjectMocks
    private PaymentService paymentService;

    private CreatePaymentRequest request;
    private Payment paymentEntity;
    private PaymentResponse response;

    @BeforeEach
    void setUp() {
        request = new CreatePaymentRequest(100L, 42L, new BigDecimal("999.99"));

        paymentEntity = Payment.builder()
                .orderId(100L)
                .userId(42L)
                .paymentAmount(new BigDecimal("999.99"))
                .status(PaymentStatus.SUCCESS)
                .timestamp(LocalDateTime.now())
                .build();

        response = new PaymentResponse(
                "mock-id", 100L, 42L, "SUCCESS",
                LocalDateTime.now(), new BigDecimal("999.99")
        );
    }

    @Test
    void shouldCreatePaymentSuccessfully() {
        when(mapper.toEntity(any(CreatePaymentRequest.class))).thenReturn(paymentEntity);
        when(paymentRepository.save(any(Payment.class))).thenReturn(paymentEntity);
        when(mapper.toResponse(any(Payment.class))).thenReturn(response);

        PaymentResponse result = paymentService.createPayment(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("mock-id");
        assertThat(result.orderId()).isEqualTo(100L);
        assertThat(result.status()).isEqualTo("SUCCESS");

        verify(mapper, times(1)).toEntity(request);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(mapper, times(1)).toResponse(any(Payment.class));
    }

    @Test
    void shouldSetTimestampOnCreation() {
        when(mapper.toEntity(any())).thenReturn(paymentEntity);
        when(paymentRepository.save(any())).thenReturn(paymentEntity);
        when(mapper.toResponse(any())).thenReturn(response);

        paymentService.createPayment(request);

        assertThat(paymentEntity.getTimestamp()).isNotNull();
        assertThat(paymentEntity.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void shouldHandleFailedPaymentStatus() {
        paymentEntity.setStatus(PaymentStatus.FAILED);
        response = new PaymentResponse(
                "mock-id", 100L, 42L, "FAILED",
                LocalDateTime.now(), new BigDecimal("999.99")
        );

        when(mapper.toEntity(any())).thenReturn(paymentEntity);
        when(paymentRepository.save(any())).thenReturn(paymentEntity);
        when(mapper.toResponse(any())).thenReturn(response);

        PaymentResponse result = paymentService.createPayment(request);

        assertThat(result.status()).isEqualTo("FAILED");
    }
}