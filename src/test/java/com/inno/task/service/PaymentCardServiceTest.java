package com.inno.task.service;

import com.inno.task.userservice.dto.paymentcard.CreatePaymentCardRequest;
import com.inno.task.userservice.entity.PaymentCard;
import com.inno.task.userservice.entity.User;
import com.inno.task.userservice.exception.NotFoundException;
import com.inno.task.userservice.exception.ValidationException;
import com.inno.task.userservice.mapper.PaymentCardMapper;
import com.inno.task.userservice.repository.PaymentCardRepository;
import com.inno.task.userservice.repository.UserRepository;
import com.inno.task.userservice.service.PaymentCardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceTest {

    @Mock private PaymentCardRepository cardRepository;
    @Mock private UserRepository userRepository;
    @Mock private PaymentCardMapper cardMapper;

    @InjectMocks private PaymentCardService service;

    //normal test
    @Test
    void createCard_success() {

        User user = new User();
        CreatePaymentCardRequest request = new CreatePaymentCardRequest();
        PaymentCard card = new PaymentCard();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.countActiveCardsByUserId(1L)).thenReturn(0L); // Лимит не превышен
        when(cardMapper.toEntity(request)).thenReturn(card);
        when(cardRepository.save(any())).thenReturn(card);


        service.createCard(1L, request);

        verify(cardRepository, times(1)).save(any());
    }

    //not found test
    @Test
    void createCard_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.createCard(99L, new CreatePaymentCardRequest()));
    }

    //limit test
    @Test
    void createCard_limitExceeded() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(cardRepository.countActiveCardsByUserId(1L)).thenReturn(5L); // Уже 5 карт

        assertThrows(ValidationException.class, () -> service.createCard(1L, new CreatePaymentCardRequest()));
    }
}