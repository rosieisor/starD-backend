package com.web.stard.domain.chat_stomp.api;

import com.web.stard.domain.chat_stomp.application.ChatRoomService;
import com.web.stard.domain.chat_stomp.domain.ChatMessage;
import com.web.stard.domain.chat_stomp.domain.ChatRoom;
import com.web.stard.domain.chat_stomp.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatService;
    private final ChatMessageRepository chatMessageRepository;

    // 채팅방 생성
    @PostMapping("/room")
    public ChatRoom createRoom(@RequestBody Map<String, Object> requestPayload) {
        String id = (String) requestPayload.get("studyId");

        Integer intId = Integer.parseInt(id);
        Long studyId = intId.longValue();
        return chatService.createRoom(studyId);
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{studyId}")
    @ResponseBody
    public ChatRoom getRoom(@PathVariable(name = "studyId") Long studyId) {
        return chatService.findByStudyId(studyId);
    }

    // 채팅 내역
    @GetMapping("/history/{studyId}")
    public List<ChatMessage> getChatHistory(@PathVariable(name = "studyId") Long studyId) {
        return chatMessageRepository.findByStudyId(studyId);
    }

}