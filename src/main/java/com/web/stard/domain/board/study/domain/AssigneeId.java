package com.web.stard.domain.board.study.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssigneeId implements Serializable {
    private Long toDo;
    private String member;
}
