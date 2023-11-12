package com.web.stard.dto;

import com.web.stard.domain.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class ToDoDto {
    private Long id;
    private Study study;
    private String task; // 담당 업무

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dueDate; // TO DO 날짜

    private List<Assignee> assignees; // 담당자

    public ToDoDto() {
    }

    public ToDoDto(Long id, Study study, String task, LocalDateTime dueDate) {
        this.id = id;
        this.study = study;
        this.task = task;
        this.dueDate = dueDate;
    }

    public ToDoDto(Long id, Study study, String task, LocalDateTime dueDate, List<Assignee> assignees) {
        this.id = id;
        this.study = study;
        this.task = task;
        this.dueDate = dueDate;
        this.assignees = assignees;
    }

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
