package com.web.stard.domain.notification.repository;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByReceiver(Member member);    // 알림 전체 목록 조회

}
