package com.web.stard.domain.chat_stomp.application;

import com.web.stard.domain.chat_stomp.domain.ChatMessage;
import com.web.stard.domain.chat_stomp.repository.ChatMessageRepository;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final MemberService memberService;

    public ChatMessage saveChatMessage(ChatMessage chatMessage, Authentication authentication) {
        String userId = authentication.getName();
        Member member = memberService.find(userId);

        chatMessage.setMember(member);

        return chatMessageRepository.save(chatMessage);
    }

}
