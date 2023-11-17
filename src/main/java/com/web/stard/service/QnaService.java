package com.web.stard.service;

import com.web.stard.domain.*;
import com.web.stard.repository.PostRepository;
import com.web.stard.repository.StarScrapRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Transactional
@Getter @Setter
@AllArgsConstructor
@Service
public class QnaService {

    MemberService memberService;
    PostRepository postRepository;
    StarScrapRepository starScrapRepository;

    // qna 등록
    public Post createQna(Post post, Authentication authentication) {
        String userId = authentication.getName();
        Member member = memberService.find(userId);

        post.setMember(member);
        post.setType(PostType.QNA);

        return postRepository.save(post);
    }

    // qna 리스트 조회 (비회원은 리스트 조회 가능)
    public List<Post> getAllQna(int page) {

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);
        // page -> 배열 인덱스처럼 들어가서 -1 해야 함
        // 한 페이지에 Post 10개 (개수는 추후 수정)
        return postRepository.findByType(PostType.QNA, pageable);
    }

    // qna 상세 조회
    public Post getQnaDetail(Long id, String userId) {
        Optional<Post> result = postRepository.findByIdAndType(id, PostType.QNA);
        if (result.isPresent()) {
            Post post = result.get();

            if (userId != null) {
                if (!post.getMember().getId().equals(userId)) {
                    // 작성자 != 현재 로그인 한 유저
                    post.setViewCount(post.getViewCount()+1);
                    postRepository.save(post);
                }
            }

            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(post, ActType.STAR, PostType.QNA);

            post.setStarCount(allStarList.size());

            return post;
        }
        return null;
    }

    // qna 수정
    public Post updateQna(Long id, Post requestPost, Authentication authentication) {
        Member member = memberService.find(authentication.getName());
        Post post = getQnaDetail(id,null);

        if (member.getId().equals(post.getMember().getId())) {  // 작성자일 때
            post.setTitle(requestPost.getTitle());
            post.setContent(requestPost.getContent());

            postRepository.save(post);
        }

        return post;
    }

    // qna 삭제
    public void deleteQna(Long postId, Authentication authentication) {
        String userId = authentication.getName();
        Role userRole = memberService.find(userId).getRoles();

        Optional<Post> optionalPost = postRepository.findById(postId);

        optionalPost.ifPresent(post -> {
            if (userRole == Role.ADMIN    // 관리자이거나
                    || post.getMember().getId().equals(userId)) {   // 작성자일 때
                postRepository.deleteById(postId);
            }
        });
    }

    // faq, qna 순 최신 순 리스트 보기
    public List<Post> getAllFaqsAndQnas() {
        List<Post> faqs = postRepository.findByTypeOrderByCreatedAtDesc(PostType.FAQ);
        for (Post p : faqs) { // 공감 수
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.FAQ);
            p.setStarCount(allStarList.size());
        }

        List<Post> qnas = postRepository.findByTypeOrderByCreatedAtDesc(PostType.QNA);
        for (Post p : qnas) { // 공감 수
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.QNA);
            p.setStarCount(allStarList.size());
        }

        List<Post> allFaqsAndQnas = new ArrayList<>();
        allFaqsAndQnas.addAll(faqs);
        allFaqsAndQnas.addAll(qnas);

        return allFaqsAndQnas;
    }
}
