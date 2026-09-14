package com.inno.task.userservice.dto.paymentcard;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreatePaymentCardRequest {

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^\\d{16}$", message = "Card number must be 16 digits")
    private String number;

    @NotBlank(message = "Card holder is required")
    @Size(max = 200, message = "Holder name must be less than 200 characters")
    private String holder;

    @NotNull(message = "Expiration date is required")
    @FutureOrPresent(message = "Expiration date cannot be in the past")
    private LocalDate expirationDate;
}