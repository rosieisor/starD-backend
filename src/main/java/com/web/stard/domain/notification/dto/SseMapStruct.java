package com.web.stard.domain.notification.dto;

import com.web.stard.domain.notification.domain.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SseMapStruct {

    SseMapStruct SSE_MAP_STRUCT = Mappers.getMapper(SseMapStruct.class);

    ResponseNotificationDto toResponseNotification (Notification notification);

}
