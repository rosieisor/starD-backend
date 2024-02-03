package com.web.stard.domain.chat_stomp.repository;

import com.web.stard.domain.chat_stomp.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByStudyId(Long studyId);
}