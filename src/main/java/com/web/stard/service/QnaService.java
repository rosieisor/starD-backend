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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Transactional
@Getter @Setter
@AllArgsConstructor
@Service
public class QnaService {

    MemberService memberService;
    PostRepository postRepository;

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
    public Post getQnaDetail(Long id) {
        Optional<Post> result = postRepository.findByIdAndType(id, PostType.QNA);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    // qna 수정
    public Post updateQna(Long id, Post requestPost, Authentication authentication) {
        String userId = authentication.getName();
        Post post = getQnaDetail(id);

        if (post.getMember().getId().equals(userId)) {  // 작성자일 때
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


}
