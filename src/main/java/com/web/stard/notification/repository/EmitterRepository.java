package com.web.stard.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);   // Emitter 저장
    void saveEventCache(String emitterId, Object event);    // 이벤트 저장
    SseEmitter get(String emiiterId);   // Emitter 찾기
    Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId);     // 해당 회원과 관련된 모든 Emitter find
    Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId);      // 해당 회원과 관련된 모든 이벤트 find
    void deleteById(String id);     // Emitter 삭제
    void deleteAllEmitterStartWithId(String memberId);      // 해당 회원과 관련된 모든 Emitter 삭제
    void deleteAllEventCacheStartWithId(String memberId);       // 해당 회원과 관련된 모든 이벤트 삭제


}
