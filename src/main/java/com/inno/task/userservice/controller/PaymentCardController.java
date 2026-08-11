package com.inno.task.userservice.controller;

import com.inno.task.userservice.dto.paymentcard.CreatePaymentCardRequest;
import com.inno.task.userservice.dto.paymentcard.PaymentCardSummaryDto;
import com.inno.task.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/cards")
@RequiredArgsConstructor
public class PaymentCardController {

    private final PaymentCardService cardService;

    //create + check for limit
    @PostMapping
    public ResponseEntity<PaymentCardSummaryDto> createCard(
            @PathVariable Long userId,
            @Valid @RequestBody CreatePaymentCardRequest request) {

        PaymentCardSummaryDto card = cardService.createCard(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    //find all by userID
    @GetMapping
    public ResponseEntity<List<PaymentCardSummaryDto>> getCardsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cardService.getCardsByUserId(userId));
    }

    //find by cardID
    @GetMapping("/{cardId}")
    public ResponseEntity<PaymentCardSummaryDto> getCardById(
            @PathVariable Long userId,
            @PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.getCardById(cardId));
    }

    //actv/deactv
    @PatchMapping("/{cardId}/status")
    public ResponseEntity<Void> setActiveStatus(
            @PathVariable Long userId,
            @PathVariable Long cardId,
            @RequestParam boolean active) {

        cardService.setActiveStatus(cardId, active);
        return ResponseEntity.noContent().build();
    }
}