package com.inno.task.userservice.service;

import com.inno.task.event.UserEmailUpdatedEvent;
import com.inno.task.event.UserStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishEmailUpdate(String login, String oldEmail, String newEmail) {
        var event = new UserEmailUpdatedEvent(login, oldEmail, newEmail);
        kafkaTemplate.send("user.email.updated", login, event);
        log.info("Published email update for {}", login);
    }

    public void publishStatusChange(String login, boolean active) {
        var event = new UserStatusChangedEvent(login, active);
        kafkaTemplate.send("user.status.changed", login, event);
        log.info("Published status change for {} to {}", login, active);
    }
}