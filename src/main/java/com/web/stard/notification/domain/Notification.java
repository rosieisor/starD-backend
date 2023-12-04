package com.web.stard.notification.domain;

import com.web.stard.domain.BaseEntity;
import com.web.stard.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Embedded
    private NotificationContent content;    // 알림 내용

    @Embedded
    private RelatedURL url;     // 관련 링크

    @Column(nullable = false)
    private Boolean isRead;     // 조회 여부

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)    // TODO 연관 관계 삭제 찾아보기
    private Member receiver;

    @Builder
    public Notification(Member receiver, NotificationType notificationType, String content, String url, Boolean isRead) {
        this.receiver = receiver;
        this.notificationType = notificationType;
        this.content = new NotificationContent(content);
        this.url = new RelatedURL(url);
        this.isRead = isRead;
    }

    public String getContent() {
        return content.getContent();
    }

    public String getUrl() {
        return url.getUrl();
    }

    public Long getId() { return this.id; }

    public boolean getIsRead() { return this.isRead; }
}
