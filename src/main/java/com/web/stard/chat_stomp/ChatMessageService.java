package com.web.stard.chat_stomp;

import com.web.stard.domain.Member;
import com.web.stard.service.MemberService;
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
