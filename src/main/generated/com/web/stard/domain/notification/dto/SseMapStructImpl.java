package com.web.stard.domain.notification.dto;

import com.web.stard.domain.notification.domain.Notification;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-04T15:46:49+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
public class SseMapStructImpl implements SseMapStruct {

    @Override
    public ResponseNotificationDto toResponseNotification(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        ResponseNotificationDto.ResponseNotificationDtoBuilder responseNotificationDto = ResponseNotificationDto.builder();

        responseNotificationDto.notification( notification );

        return responseNotificationDto.build();
    }
}
