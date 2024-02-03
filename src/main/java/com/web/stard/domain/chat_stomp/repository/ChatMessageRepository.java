package com.web.stard.domain.chat_stomp.repository;

import com.web.stard.domain.chat_stomp.domain.ChatMessage;
import com.web.stard.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByStudyId(Long studyId);

    List<ChatMessage> findByMember(Member member);
}
