package com.web.stard.repository;

import com.web.stard.domain.Member;
import com.web.stard.domain.PostType;
import com.web.stard.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    /* 댓글 전체 조회 (타입 상관 없이 최근 순, 페이징) */
    Page<Reply> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /* post 게시글 아이디 별 댓글 전체 조회 (생성일 순) */
    List<Reply> findAllByPostIdOrderByCreatedAtAsc(Long postId);

    /* study 게시글 아이디 별 댓글 전체 조회 (생성일 순) */
    List<Reply> findAllByStudyIdOrderByCreatedAtAsc(Long studyId);

    /* studyPost 게시글 아이디 별 댓글 전체 조회 (생성일 순) */
    List<Reply> findAllByStudyPostIdOrderByCreatedAtAsc(Long studyId);

    List<Reply> findAllByMember(Member member);

    Page<Reply> findByMember(Member member, Pageable pageable);
}
