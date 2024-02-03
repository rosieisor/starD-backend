package com.web.stard.domain.notification.controller;

import com.web.stard.domain.notification.service.NotificationService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation(value = "클라이언트에서 구독하기 위한 subscribe 메서드 / 로그인 시 사용")
    @ApiImplicitParam(name = "lastEventId", value = "SSE 연결이 끊겼을 때, 클라이언트가 수신한 마지막 데이터의 id")
    public SseEmitter subscribe(Authentication authentication,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        String userId = authentication.getName();
        return notificationService.subscribe(userId, lastEventId);
    }


    @Description("서버에서 클라이언트로 알림을 주기 위한 메서드")
    @PostMapping("/send-data/{id}")
    public void sendData(@PathVariable String id) {
        notificationService.notify(id, "data");
    }

}
