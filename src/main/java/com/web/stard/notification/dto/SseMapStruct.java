package com.web.stard.notification.dto;

import com.web.stard.notification.domain.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SseMapStruct {

    SseMapStruct SSE_MAP_STRUCT = Mappers.getMapper(SseMapStruct.class);

    ResponseNotificationDto toResponseNotification (Notification notification);

}
