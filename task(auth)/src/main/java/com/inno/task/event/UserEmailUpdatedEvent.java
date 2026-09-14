package com.inno.task.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserEmailUpdatedEvent {
    private String login;
    private String oldEmail;
    private String newEmail;
}
