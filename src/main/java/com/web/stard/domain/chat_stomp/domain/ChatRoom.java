package com.web.stard.domain.chat_stomp.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
public class ChatRoom {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long roomId;
    @NotNull
    private Long studyId;

    public ChatRoom() {
    }

    public ChatRoom(long studyId) {
        this.studyId = studyId;
    }
}