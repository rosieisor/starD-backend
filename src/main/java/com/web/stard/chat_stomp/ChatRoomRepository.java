package com.web.stard.chat_stomp;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByStudyId(Long studyId);
}