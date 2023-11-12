package com.web.stard.chat_stomp;

import com.web.stard.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByStudyId(Long studyId);

    List<ChatMessage> findByMember(Member member);
}
