package com.web.stard.domain.notification.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

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
