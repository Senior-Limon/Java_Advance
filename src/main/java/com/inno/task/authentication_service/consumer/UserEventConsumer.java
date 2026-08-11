package com.inno.task.authentication_service.consumer;

import com.inno.task.authentication_service.repository.UserRepository;
import com.inno.task.event.UserEmailUpdatedEvent;
import com.inno.task.event.UserStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {
    private final UserRepository userRepository;

    @KafkaListener(topics = "user.email.updated", groupId = "auth-email-group")
    public void onEmailUpdated(UserEmailUpdatedEvent event) {
        userRepository.findByLogin(event.getLogin())
                .ifPresent(u -> {
                    u.setLogin(event.getNewEmail());
                    userRepository.save(u);
                    log.info("Updated login/email for {}", event.getLogin());
                });
    }

    @KafkaListener(topics = "user.status.changed", groupId = "auth-status-group")
    public void onStatusChanged(UserStatusChangedEvent event) {
        userRepository.findByLogin(event.getLogin())
                .ifPresent(u -> {
                    if (u.getActive() != event.isActive()) {
                        u.setActive(event.isActive());
                        userRepository.save(u);
                        log.info("Updated status for {} to {}", event.getLogin(), event.isActive());
                    }
                });
    }
}