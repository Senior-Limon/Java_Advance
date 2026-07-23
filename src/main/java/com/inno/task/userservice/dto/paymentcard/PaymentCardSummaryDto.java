package com.inno.task.userservice.dto.paymentcard;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class PaymentCardSummaryDto {
    private Long id;
    private String numberMasked;
    private String holder;
    private LocalDate expirationDate;
    private Boolean active;
}