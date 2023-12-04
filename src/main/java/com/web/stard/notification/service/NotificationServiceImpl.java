package com.web.stard.notification.service;

import com.web.stard.domain.Member;
import com.web.stard.notification.domain.Notification;
import com.web.stard.notification.domain.NotificationType;
import com.web.stard.notification.repository.EmitterRepository;
import com.web.stard.notification.repository.NotificationRepository;
import com.web.stard.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

import static com.web.stard.notification.dto.SseMapStruct.SSE_MAP_STRUCT;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;    // 기본 타임아웃 설정 (1시간)

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    /**
     * 클라이언트가 구독을 위해 호출하는 메서드.
     *
     * @param userId - 구독하는 클라이언트의 사용자 아이디.
     * @return SseEmitter - 서버에서 보낸 이벤트 Emitter
     */
    public SseEmitter subscribe(String userId, String lastEventId) {

        String emitterId = userId + "_" + System.currentTimeMillis();
        SseEmitter emitter = createEmitter(emitterId);
        sendToClient(emitter, emitterId, "EventStream Created. [userId=" + userId + "]");   // 최초 연결 시 더미 데이터 전송

        if (!lastEventId.isEmpty()) {   // lastEventId 값이 있는 경우 유실된 데이터 다시 전송
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(userId);
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    /**
     * 서버의 이벤트를 클라이언트에게 보내는 메서드
     * 다른 서비스 로직에서 이 메서드를 사용해 데이터를 Object event에 넣고 전송하면 된다.
     *
     * @param userId - 메세지를 전송할 사용자의 아이디.
     * @param event  - 전송할 이벤트 객체.
     */
    public void notify(String userId, Object event) {
        SseEmitter emitter = emitterRepository.get(userId);
        sendToClient(emitter, userId, event);
    }

    /**
     * 클라이언트에게 데이터를 전송
     *
     * @param id   - 데이터를 받을 사용자의 아이디.
     * @param data - 전송할 데이터.
     */
    public void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(id);
            emitter.completeWithError(exception);
        }
    }


    /**
     * 사용자 아이디를 기반으로 이벤트 Emitter를 생성
     *
     * @param userId - 사용자 아이디.
     * @return SseEmitter - 생성된 이벤트 Emitter.
     */
    public SseEmitter createEmitter(String userId) {

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, emitter);

        // Emitter가 완료될 때 (모든 데이터가 성공적으로 전송된 상태) Emitter 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(userId));
        // Emitter가 타임아웃 되었을 때 (지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter 삭제
        emitter.onTimeout(() -> emitterRepository.deleteById(userId));

        return emitter;
    }

    @Override
    public void send(Member receiver, NotificationType notificationType, String content, String url) {
        Notification notification = notificationRepository.save(createNotification(receiver, notificationType, content, url));
        String memberId = String.valueOf(receiver.getId());

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(memberId);
        sseEmitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendToClient(emitter, key, SSE_MAP_STRUCT.toResponseNotification(notification));
                }
        );

    }

    private Notification createNotification(Member receiver, NotificationType notificationType, String content, String url) {
        return Notification.builder()
                .receiver(receiver)
                .notificationType(notificationType)
                .content(content)
                .url(url)
                .isRead(false)
                .build();
    }


    // 댓글 알림 - 게시글 작성자 에게
//    public void notifyComment(Long postId) {
//
//        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
//
//        Long userId = post.getUser().getId();
//
//        if (NotificationController.sseEmitters.containsKey(userId)) {
//            SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);
//            try {
//                sseEmitter.send(SseEmitter.event().name("addComment").data("댓글이 달렸습니다."));
//            } catch (Exception e) {
//                NotificationController.sseEmitters.remove(userId);
//            }
//        }
//    }

}
