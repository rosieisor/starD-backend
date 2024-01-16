package com.web.stard.service;

import com.web.stard.domain.*;
import com.web.stard.repository.PostRepository;
import com.web.stard.repository.StarScrapRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Getter @Setter
@AllArgsConstructor
@Service
public class FaqService {

    MemberService memberService;
    PostRepository postRepository;
    StarScrapRepository starScrapRepository;

    // faq 등록
    public Post createFaq(Post post, Authentication authentication) {
        String userId = authentication.getName();
        Member member = memberService.find(userId);

        post.setMember(member);
        post.setType(PostType.FAQ);

        return postRepository.save(post);
    }

    // faq 리스트 조회(페이지화o)
    public Page<Post> getAllFaq(int page) {

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);
        // page -> 배열 인덱스처럼 들어가서 -1 해야 함
        // 한 페이지에 Post 10개 (개수는 추후 수정)
        return postRepository.findByType(PostType.FAQ, pageable);
    }

    // faq 상세 조회
    public Post getFaqDetail(Long id, String userId) {
        Optional<Post> result = postRepository.findByIdAndType(id, PostType.FAQ);
        if (result.isPresent()) {
            Post post = result.get();

            if (userId != null) {
                if (!post.getMember().getId().equals(userId)) {
                    // 작성자 != 현재 로그인 한 유저
                    post.setViewCount(post.getViewCount()+1);
                    postRepository.save(post);
                }
            }

            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(post, ActType.STAR, PostType.FAQ);

            post.setStarCount(allStarList.size());

            return post;
        }
        return null;
    }

    // faq 수정
    public Post updateFaq(Long id, Post requestPost, Authentication authentication) {
        Member member = memberService.find(authentication.getName());
        Post post = getFaqDetail(id, member.getAuthorities().toString());

        post.setTitle(requestPost.getTitle());
        post.setContent(requestPost.getContent());
        post.setMember(member);

        postRepository.save(post);

        return post;
    }

    // faq 삭제
    public void deleteFaq(Long postId, Authentication authentication) {
        String userId = authentication.getName();
        Role userRole = memberService.find(userId).getRoles();

        Optional<Post> optionalPost = postRepository.findById(postId);

        optionalPost.ifPresent(post -> {
            if (userRole == Role.ADMIN)  {   // 관리자일때만
                postRepository.deleteById(postId);
            }
        });
    }
}
