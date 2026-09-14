package com.inno.task.userservice.mapper;

import com.inno.task.userservice.dto.paymentcard.CreatePaymentCardRequest;
import com.inno.task.userservice.dto.paymentcard.PaymentCardSummaryDto;
import com.inno.task.userservice.entity.PaymentCard;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentCardMapper {

    @Mapping(source = "number", target = "numberMasked", qualifiedByName = "maskNumber")
    PaymentCardSummaryDto toSummary(PaymentCard card);

    //why not?
    @Named("maskNumber")
    default String maskNumber(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", constant = "true")
    PaymentCard toEntity(CreatePaymentCardRequest request);
}