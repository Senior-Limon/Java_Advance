package com.inno.task.userservice.service;

import com.inno.task.userservice.dto.paymentcard.CreatePaymentCardRequest;
import com.inno.task.userservice.dto.paymentcard.PaymentCardSummaryDto;
import com.inno.task.userservice.entity.PaymentCard;
import com.inno.task.userservice.entity.User;
import com.inno.task.userservice.exception.NotFoundException;
import com.inno.task.userservice.exception.ValidationException;
import com.inno.task.userservice.mapper.PaymentCardMapper;
import com.inno.task.userservice.repository.PaymentCardRepository;
import com.inno.task.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCardService {

    private final PaymentCardRepository cardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper cardMapper;
    private final UserService userService;


    private static final int MAX_ACTIVE_CARDS = 5;
    private static final String USER_CARDS_CACHE = "userCards";


    @Transactional
    @CacheEvict(value = USER_CARDS_CACHE, key = "#userId")
    public PaymentCardSummaryDto createCard(Long userId, CreatePaymentCardRequest request) {
        log.info("Creating payment card for user {}", userId);
        User user = userService.getUserEntityById(userId);

        long activeCardsCount = cardRepository.countActiveCardsByUserId(userId);
        if (activeCardsCount >= MAX_ACTIVE_CARDS) {
            throw new ValidationException("Cannot add more than " + MAX_ACTIVE_CARDS + " active cards");
        }
        PaymentCard card = cardMapper.toEntity(request);
        card.setUser(user);
        user.addPaymentCard(card);
        log.info("The credit card creation was successful for user with id: {}", userId);
        return cardMapper.toSummary(cardRepository.save(card));
    }

    //find by userID
    @Cacheable(value = USER_CARDS_CACHE, key = "#userId")
    public List<PaymentCardSummaryDto> getCardsByUserId(Long userId) {
        //check if user exists via UserService method
        userService.getUserById(userId);
        return cardRepository.findByUserId(userId).stream()
                .map(cardMapper::toSummary)
                .toList();
    }

    //find by cardID
    public PaymentCardSummaryDto getCardById(Long id) {
        PaymentCard card = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found with id: " + id));
        return cardMapper.toSummary(card);
    }

    //actv/deactv
    @Transactional
    @CacheEvict(value = USER_CARDS_CACHE, allEntries = true)
    public void setActiveStatus(Long id, boolean active) {
        if (!cardRepository.existsById(id)) {
            throw new NotFoundException("Card not found with id: " + id);
        }

        log.info(active ? "activate card with id: {}" : "deactivate card with id: {}", id);
        cardRepository.setActiveStatus(id, active);
    }
}