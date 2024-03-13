package com.web.stard.domain.notification.controller;

import com.web.stard.domain.notification.service.NotificationService;
import com.web.stard.global.config.jwt.JwtTokenProvider;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    // EventSource API는 기본적으로 HTTP 헤더를 수정할 수 없어 Authorization 헤더 추가x -> 쿼리 파라미터나 쿠키를 사용하여 토큰을 전송
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation(value = "클라이언트에서 구독하기 위한 subscribe 메서드 / 로그인 시 사용")
    @ApiImplicitParam(name = "lastEventId", value = "SSE 연결이 끊겼을 때, 클라이언트가 수신한 마지막 데이터의 id")
    public SseEmitter subscribe(@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
                                @RequestParam(value = "token", required = false) String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String userId = authentication.getName();
        return notificationService.subscribe(userId, lastEventId);
    }


//    @Description("서버에서 클라이언트로 알림을 주기 위한 메서드")
//    @PostMapping("/send-data/{id}")
//    public void sendData(@PathVariable String id) {
//        notificationService.notify(id, "data");
//    }

    // TODO emitter가 null이라는 NullPointerException 발생
    @Description("서버에서 클라이언트로 알림을 주기 위한 메서드")
    @PostMapping("/send-data")
    public void sendData(@RequestParam(value = "token", required = false) String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String userId = authentication.getName();
        notificationService.notify(userId, "data");
    }

    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation(value = "클라이언트에서 구독하기 위한 subscribe 메서드 / 로그인 시 사용")
    @ApiImplicitParam(name = "lastEventId", value = "SSE 연결이 끊겼을 때, 클라이언트가 수신한 마지막 데이터의 id")
    public SseEmitter subscribeByUserId(@PathVariable String userId,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(userId, lastEventId);
    }

}
