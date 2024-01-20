package com.web.stard.service;

import com.web.stard.domain.*;
import com.web.stard.repository.PostRepository;
import com.web.stard.repository.StarScrapRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@Getter @Setter
@AllArgsConstructor
@Service
public class NoticeService {

    MemberService memberService;
    PostRepository postRepository;
    StarScrapRepository starScrapRepository;

    // Notice 등록
    public Post createNotice(Post post, Authentication authentication) {
        String userId = authentication.getName();
        Member member = memberService.find(userId);

        post.setMember(member);
        post.setType(PostType.NOTICE);

        return postRepository.save(post);
    }

    // Notice 리스트 조회 (페이지화x)
    public List<Post> getAllNotice() {
        List<Post> posts = postRepository.findByTypeOrderByCreatedAtDesc(PostType.NOTICE);

        for (Post p : posts) { // 스크랩 수, 공감 수
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.NOTICE);

            p.setStarCount(allStarList.size());
        }

        return posts;
    }

    // Notice 리스트 조회 (페이지화o)
    public Page<Post> getAllNotice(int page) {

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);
        // page -> 배열 인덱스처럼 들어가서 -1 해야 함
        // 한 페이지에 Post 10개 (개수는 추후 수정)

        Page<Post> posts = postRepository.findByType(PostType.NOTICE, pageable);

        for (Post p : posts) { // 스크랩 수, 공감 수
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.NOTICE);
            p.setStarCount(allStarList.size());
        }

        return posts;
    }

    // Notice 상세 조회 (회원도 상세 조회 가능)
    public Post getNoticeDetail(Long id, String userId) {
        Optional<Post> result = postRepository.findByIdAndType(id, PostType.NOTICE);
        if (result.isPresent()) {
            Post post = result.get();

            if (userId != null) {
                if (!post.getMember().getId().equals(userId)) {
                    // 작성자 != 현재 로그인 한 유저
                    post.setViewCount(post.getViewCount()+1);
                    postRepository.save(post);
                }
            }

            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(post, ActType.STAR, PostType.NOTICE);

            post.setStarCount(allStarList.size());

            return post;
        }
        return null;
    }

    // Notice 수정
    public Post updateNotice(Long id, Post requestPost, Authentication authentication) {
        Member member = memberService.find(authentication.getName());
        Post post = getNoticeDetail(id, member.getAuthorities().toString());

        post.setTitle(requestPost.getTitle());
        post.setContent(requestPost.getContent());
        post.setMember(member);

        postRepository.save(post);

        return post;
    }

    // Notice 삭제
    public void deleteNotice(Long id, Authentication authentication) {
        String userId = authentication.getName();
        Role userRole = memberService.find(userId).getRoles();

        Optional<Post> optionalPost = postRepository.findById(id);

        optionalPost.ifPresent(post -> {
            if (userRole == Role.ADMIN ) {   // 관리자일때만
                postRepository.deleteById(id);
            }
        });
    }

    // id로 타입 찾기
    public Optional<Post> findTypeById(Long id, Authentication authentication) {
        return postRepository.findById(id);
    }

    // 전체 검색
    public List<Post> searchNoticePost(String searchType, String searchWord) {
        List<Post> posts = null;

        if (searchType.equals("제목")) {
            posts = postRepository.findByTypeAndTitleContainingOrderByCreatedAtDesc(PostType.NOTICE, searchWord);
        } else if (searchType.equals("내용")) {
            posts = postRepository.findByTypeAndContentContainingOrderByCreatedAtDesc(PostType.NOTICE, searchWord);
        }

        for (Post p : posts) { // 스크랩 수, 공감 수
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.NOTICE);

            p.setStarCount(allStarList.size());
        }

        return posts;
    }
}
