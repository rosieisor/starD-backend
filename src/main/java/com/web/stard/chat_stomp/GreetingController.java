package com.web.stard.chat_stomp;

import com.web.stard.config.jwt.JwtTokenProvider;
import com.web.stard.domain.Member;
import com.web.stard.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

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