package com.web.stard.domain.chat_stomp.api;

import com.web.stard.global.config.jwt.JwtTokenProvider;
import com.web.stard.domain.chat_stomp.application.ChatMessageService;
import com.web.stard.domain.chat_stomp.domain.ChatMessage;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
@RequiredArgsConstructor
public class GreetingController {

    final ChatMessageService chatMessageService;
    final MemberService memberService;
    final JwtTokenProvider jwtTokenProvider;

    public Authentication getUserAuthenticationFromToken(String accessToken) {
        jwtTokenProvider.validateToken(accessToken);
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        System.out.println("***token: " + accessToken);
        System.out.println("***auth: " + authentication);
        System.out.println("***name: " + authentication.getName());

        return authentication;
    }

    // 입장
    @MessageMapping("/enter/{studyId}")
    @SendTo("/topic/greetings/{studyId}")
    public ChatMessage enter(ChatMessage message, SimpMessageHeaderAccessor session) {
        Authentication authentication = getUserAuthenticationFromToken(session.getFirstNativeHeader("Authorization"));
        Member user = memberService.findNickNameByAuthentication(authentication);
        return new ChatMessage(HtmlUtils.htmlEscape(user.getNickname() + "님이 입장하였습니다."));
    }

    // 퇴장
    @MessageMapping("/exit/{studyId}")
    @SendTo("/topic/greetings/{studyId}")
    public ChatMessage exit(ChatMessage message, SimpMessageHeaderAccessor session) throws Exception {
        Authentication authentication = getUserAuthenticationFromToken(session.getFirstNativeHeader("Authorization"));
        Member user = memberService.findNickNameByAuthentication(authentication);
        return new ChatMessage(HtmlUtils.htmlEscape(user.getNickname() + "님이 퇴장하였습니다."));
    }

    // 채팅
    @MessageMapping("/chat/{studyId}")
    @SendTo("/topic/greetings/{studyId}")
    public ChatMessage chat(ChatMessage message, SimpMessageHeaderAccessor session) throws Exception {
        // 토큰 추출
        Authentication authentication = getUserAuthenticationFromToken(session.getFirstNativeHeader("Authorization"));

        // 채팅 메시지 저장
        ChatMessage savedChat = chatMessageService.saveChatMessage(message, authentication);

        return savedChat;
    }

}