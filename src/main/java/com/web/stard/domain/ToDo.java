package com.web.stard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter @Setter @Builder
public class ToDo {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    private String task; // 담당 업무

    @Column(name = "due_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dueDate; // TO DO 날짜



    @Transient // DB랑 매핑되지 않음
    @JsonIgnore
    private List<Assignee> assignees; // 담당자

    @Override
    public String toString() {
        String str = "ToDo{" +
                "id=" + id +
                ", study=" + study.getTitle() +
                ", task='" + task + '\'' +
                ", dueDate=" + dueDate +
                '}' + "\n" + "assignee : ";
        for (Assignee a : assignees) {
            str += a.getMember().getId() + " + " + a.isToDoStatus() + ", ";
        }

        return str;
    }
}
