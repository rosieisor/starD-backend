package com.web.stard.domain.board.global.repository;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.board.global.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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

    /* study 게시글 아이디 별 댓글 전체 조회 (생성일 순) - studypost가 null인 것 */
    List<Reply> findAllByStudyIdAndStudyPostIdIsNullOrderByCreatedAtAsc(Long studyId);
}
