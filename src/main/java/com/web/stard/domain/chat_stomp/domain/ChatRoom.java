package com.web.stard.domain.chat_stomp.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

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