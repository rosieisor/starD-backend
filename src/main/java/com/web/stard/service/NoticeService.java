package com.web.stard.service;

import com.web.stard.domain.Member;
import com.web.stard.domain.Post;
import com.web.stard.domain.PostType;
import com.web.stard.domain.Role;
import com.web.stard.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Getter @Setter
@AllArgsConstructor
@Service
public class NoticeService {

    MemberService memberService;
    PostRepository postRepository;

    // Notice 등록
    public Post createNotice(Post post, Authentication authentication) {
        String userId = authentication.getName();
        Member member = memberService.find(userId);

        post.setMember(member);
        post.setType(PostType.NOTICE);

        return postRepository.save(post);
    }

    // Notice 리스트 조회 (회원, 비회원도 리스트 조회 가능)
    public List<Post> getAllNotice(int page) {

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);
        // page -> 배열 인덱스처럼 들어가서 -1 해야 함
        // 한 페이지에 Post 10개 (개수는 추후 수정)
        return postRepository.findByType(PostType.NOTICE, pageable);
    }

    // Notice 상세 조회 (회원도 상세 조회 가능)
    public Post getNoticeDetail(Long id) {
        Optional<Post> result = postRepository.findByIdAndType(id, PostType.NOTICE);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    // Notice 수정
    public Post updateNotice(Long id, Post requestPost, Authentication authentication) {
        Member member = memberService.find(authentication.getName());
        Post post = getNoticeDetail(id);

        post.setTitle(requestPost.getTitle());
        post.setContent(requestPost.getContent());
        post.setMember(member);

        postRepository.save(post);

        return post;
    }

    // Notice 삭제
    public void deleteNotice(Long postId, Authentication authentication) {
        String userId = authentication.getName();
        Role userRole = memberService.find(userId).getRoles();

        Optional<Post> optionalPost = postRepository.findById(postId);

        optionalPost.ifPresent(post -> {
            if (userRole == Role.ADMIN ) {   // 관리자일때만
                postRepository.deleteById(postId);
            }
        });
    }


}
