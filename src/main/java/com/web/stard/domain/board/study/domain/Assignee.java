package com.web.stard.domain.board.study.domain;

import com.web.stard.domain.member.domain.Member;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Table
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter @Setter @Builder @EqualsAndHashCode
@IdClass(AssigneeId.class)
public class Assignee {
    @Id @ManyToOne
    @JoinColumn(name = "to_do_id")
    private ToDo toDo; // TO DO

    @Id @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // 담당자

    @Column(name = "to_do_status")
    private boolean toDoStatus; // TO DO 상태 (false 미완료, true 완료)


    public boolean isToDoStatus() {
        return toDoStatus;
    }

    @Override
    public String toString() {
        return "Assignee{" +
                "toDo=" + toDo.getTask() + ", " + toDo.getDueDate() + ", " + toDo.getStudy().getTitle() +
                ", member=" + member.getId() +
                ", toDoStatus=" + toDoStatus +
                '}';
    }
}
