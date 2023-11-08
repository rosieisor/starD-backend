package com.web.stard.domain;

import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter @Setter @Builder @ToString
public class Schedule {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    private String title; // 일정 이름

    @Column(name = "start_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate; // 날짜, 시간
}
