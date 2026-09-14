package com.inno.task.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserStatusChangedEvent {
    private String login;
    private boolean active;
}