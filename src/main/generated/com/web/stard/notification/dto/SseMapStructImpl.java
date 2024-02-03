package com.web.stard.notification.dto;

import com.web.stard.domain.notification.domain.Notification;
import com.web.stard.domain.notification.dto.ResponseNotificationDto;
import com.web.stard.domain.notification.dto.SseMapStruct;

import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-01-26T03:35:19+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.17 (Oracle Corporation)"
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
