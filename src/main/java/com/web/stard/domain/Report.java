package com.web.stard.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter @Setter
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne
    @JoinColumn(name = "reply_id")
    private Reply reply;

    @NotNull
    @Column(name = "table_type")
    @Enumerated(EnumType.STRING)
    private PostType tableType;    // [COMM, QNA, STUDY, REPLY]
}
