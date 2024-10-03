package com.web.stard.domain.chat_stomp.application;

import com.web.stard.domain.chat_stomp.domain.ChatRoom;
import com.web.stard.domain.chat_stomp.repository.ChatRoomRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private Map<String, ChatRoom> chatRooms;

    @PostConstruct
    //의존관게 주입완료되면 실행되는 코드
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

    //채팅방 하나 불러오기
    public ChatRoom findByStudyId(Long studyId) {
        return chatRoomRepository.findByStudyId(studyId);
    }

    //채팅방 생성
    public ChatRoom createRoom(Long studyId) {
        ChatRoom chatRoom = new ChatRoom(studyId);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
}