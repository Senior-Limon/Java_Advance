package com.inno.task.payment_service.mapper;

import com.inno.task.payment_service.dto.CreatePaymentRequest;
import com.inno.task.payment_service.dto.PaymentResponse;
import com.inno.task.payment_service.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "timestamp", ignore = true)
    Payment toEntity(CreatePaymentRequest request);

    PaymentResponse toResponse(Payment payment);
}