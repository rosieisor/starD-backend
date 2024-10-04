package com.web.stard.domain.notification.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

@Getter
@Embeddable
@NoArgsConstructor
public class NotificationContent {

    @NotNull
    private String content;

    public NotificationContent(String content) {
        this.content = content;
    }



}
