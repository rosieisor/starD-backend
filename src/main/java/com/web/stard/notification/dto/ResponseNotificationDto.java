package com.web.stard.notification.dto;

import com.web.stard.notification.domain.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseNotificationDto {

    private Long id;
    private String content;
    private String url;
    private Boolean isRead;
    private String createdAt;

    @Builder
    public ResponseNotificationDto(Notification notification) {
        this.id = notification.getId();
        this.content = notification.getContent();
        this.url = notification.getUrl();
        this.isRead = notification.getIsRead();
        this.createdAt = String.valueOf((notification.getCreatedAt()));
    }

}
