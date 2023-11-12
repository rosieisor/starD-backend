package com.web.stard.repository;

import com.web.stard.domain.Member;
import com.web.stard.domain.Post;
import com.web.stard.domain.PostType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    /* 타입 별 게시글 전체 조회 (최근 순) */
    List<Post> findByTypeOrderByCreatedAtDesc(PostType type);

    /* 타입 별 게시글 전체 조회 (최근 순 + 페이징) */
    List<Post> findByType(PostType type, Pageable pageable);

    /* 타입 별 게시글 세부 조회 */
    Optional<Post> findByIdAndType(Long id, PostType type);

    List<Post> findAllByMember(Member member);


    /* 제목 검색 (최근 순) */
    List<Post> findByTypeAndTitleContainingOrderByCreatedAtDesc(PostType type, String searchWord);
    /* 내용 검색 (최근 순) */
    List<Post> findByTypeAndContentContainingOrderByCreatedAtDesc(PostType type, String searchWord);
    /* 작성자 검색 (최근 순) */
    List<Post> findByTypeAndMemberOrderByCreatedAtDesc(PostType type, Member member);

    /* 카테고리 - 제목 검색 (최근 순) */
    List<Post> findByTypeAndCategoryAndTitleContainingOrderByCreatedAtDesc(PostType type, String category, String searchWord);
    /* 카테고리 - 내용 검색 (최근 순) */
    List<Post> findByTypeAndCategoryAndContentContainingOrderByCreatedAtDesc(PostType type, String category, String searchWord);
    /* 카테고리 - 작성자 검색 (최근 순) */
    List<Post> findByTypeAndCategoryAndMemberOrderByCreatedAtDesc(PostType type, String category, Member member);


    /* 제목 검색 (최근 순 + 페이징) */
    List<Post> findByTypeAndTitleContaining(PostType type, String searchWord, Pageable pageable);
    /* 내용 검색 (최근 순 + 페이징) */
    List<Post> findByTypeAndContentContaining(PostType type, String searchWord, Pageable pageable);
    /* 작성자 검색 (최근 순 + 페이징) */
    List<Post> findByTypeAndMember(PostType type, Member member, Pageable pageable);

    /* 카테고리 - 제목 검색 (최근 순 + 페이징) */
    List<Post> findByTypeAndCategoryAndTitleContaining(PostType type, String category, String searchWord, Pageable pageable);
    /* 카테고리 - 내용 검색 (최근 순 + 페이징) */
    List<Post> findByTypeAndCategoryAndContentContaining(PostType type, String category, String searchWord, Pageable pageable);
    /* 카테고리 - 작성자 검색 (최근 순 + 페이징) */
    List<Post> findByTypeAndCategoryAndMember(PostType type, String category, Member member, Pageable pageable);
}
