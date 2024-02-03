package com.web.stard.domain.board.community.application;

import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.global.domain.enums.ActType;
import com.web.stard.domain.board.global.domain.StarScrap;
import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.Role;
import com.web.stard.domain.board.global.repository.PostRepository;
import com.web.stard.domain.board.global.repository.StarScrapRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
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
    public Page<Post> getAllQna(int page) {

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
    public Page<Post> getAllFaqsAndQnas(int page) {
        // 정렬 방식을 최신 날짜순으로 설정
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));

        // 현재 페이지에 해당하는 데이터만큼 가져오도록 설정
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        // FAQ와 QNA를 나타내는 PostType 리스트 생성
        List<PostType> faqAndQnaTypes = Arrays.asList(PostType.FAQ, PostType.QNA);

        // FAQ와 QNA 데이터 조회
        List<Post> faqsAndQnas = postRepository.findByTypeInOrderByCreatedAtDesc(faqAndQnaTypes);

        // 각 FAQ와 QNA의 공감 수 설정
        for (Post p : faqsAndQnas) {
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, p.getType());
            p.setStarCount(allStarList.size());
        }

        // FAQ를 우선적으로 보여주기 위해 정렬
        faqsAndQnas.sort((post1, post2) -> {
            if (post1.getType() == PostType.FAQ && post2.getType() != PostType.FAQ) {
                return -1;
            } else if (post1.getType() != PostType.FAQ && post2.getType() == PostType.FAQ) {
                return 1;
            } else {
                return post2.getCreatedAt().compareTo(post1.getCreatedAt());
            }
        });

        // 최종 결과를 페이지에 맞게 자르기
        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), faqsAndQnas.size());
        List<Post> slicedResults = faqsAndQnas.subList(startIndex, endIndex);

        return new PageImpl<>(slicedResults, pageable, faqsAndQnas.size());
    }



    // 전체 검색 (전체, 제목/내용/작성자) - 페이지화x
    public List<Post> searchQnaAndFaq(String searchType, String searchWord) {
        List<Post> qnas = new ArrayList<>();
        List<Post> faqs = new ArrayList<>();

        if (searchType.equals("제목")) {
            faqs = postRepository.findByTypeAndTitleContainingOrderByCreatedAtDesc(PostType.FAQ, searchWord);
            qnas = postRepository.findByTypeAndTitleContainingOrderByCreatedAtDesc(PostType.QNA, searchWord);
        } else if (searchType.equals("내용")) {
            faqs = postRepository.findByTypeAndContentContainingOrderByCreatedAtDesc(PostType.FAQ, searchWord);
            qnas = postRepository.findByTypeAndContentContainingOrderByCreatedAtDesc(PostType.QNA, searchWord);
        } else {    // 작성자
            if (searchWord.equals("관리자")) {
                faqs = postRepository.findByTypeOrderByCreatedAtDesc(PostType.FAQ);
            }
            else {
                Member member = memberService.findByNickname(searchWord);
                if (member == null) {
                    return null;
                }
                qnas = postRepository.findByTypeAndMemberOrderByCreatedAtDesc(PostType.QNA, member);
            }
        }

        // 공감 수
        for (Post p : faqs) {
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.FAQ);

            p.setStarCount(allStarList.size());
        }

        for (Post p : qnas) {
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.QNA);

            p.setStarCount(allStarList.size());
        }

        // faq -> qna 순 합치기
        List<Post> posts = new ArrayList<>();
        posts.addAll(faqs);
        posts.addAll(qnas);

        return posts;
    }

    // 전체 검색 (전체, 제목/내용/작성자) - 페이지화o
    public Page<Post> searchQnaAndFaq(String searchType, String searchWord, int page) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        List<Post> qnas = null;
        List<Post> faqs = null;

        if (searchType.equals("제목")) {
            faqs = postRepository.findByTypeAndTitleContainingOrderByCreatedAtDesc(PostType.FAQ, searchWord);
            qnas = postRepository.findByTypeAndTitleContainingOrderByCreatedAtDesc(PostType.QNA, searchWord);
        } else if (searchType.equals("내용")) {
            faqs = postRepository.findByTypeAndContentContainingOrderByCreatedAtDesc(PostType.FAQ, searchWord);
            qnas = postRepository.findByTypeAndContentContainingOrderByCreatedAtDesc(PostType.QNA, searchWord);
        } else { // 작성자
            if (searchWord.equals("관리자")) {
                faqs = postRepository.findByTypeOrderByCreatedAtDesc(PostType.FAQ);
            } else {
                Member member = memberService.findByNickname(searchWord);
                if (member == null) {
                    return Page.empty();
                }
                qnas = postRepository.findByTypeAndMemberOrderByCreatedAtDesc(PostType.QNA, member);
            }
        }

        // 공감 수 설정
        for (Post p : faqs) {
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.FAQ);
            p.setStarCount(allStarList.size());
        }

        for (Post p : qnas) {
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.QNA);
            p.setStarCount(allStarList.size());
        }

        // faq -> qna 순 합치기
        List<Post> posts = new ArrayList<>();
        posts.addAll(faqs);
        posts.addAll(qnas);

        // 최종 결과를 페이지에 맞게 자르기
        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), posts.size());
        List<Post> slicedResults = posts.subList(startIndex, endIndex);

        return new PageImpl<>(slicedResults, pageable, posts.size());
    }

    // 카테고리별 검색 (qna/faq, 제목/내용/작성자) - 페이지화x
    public List<Post> searchQnaOrFaqByCategory(String searchType, String category, String searchWord) {
        List<Post> posts;

        PostType postType = null;

        if (category.equals("FAQ")) {
            postType = PostType.FAQ;
        }
        else if (category.equals("QNA")) {
            postType = PostType.QNA;
        }

        if (searchType.equals("제목")) {
            posts = postRepository.findByTypeAndTitleContainingOrderByCreatedAtDesc(postType, searchWord);
        } else if (searchType.equals("내용")) {
            posts = postRepository.findByTypeAndContentContainingOrderByCreatedAtDesc(postType, searchWord);
        } else {
            // faq에서 '관리자' 검색 시 faq 전체 리스트 가져오면 됨
            if (postType == PostType.FAQ) {
                if (searchWord.equals("관리자")) {
                    posts = postRepository.findByTypeOrderByCreatedAtDesc(PostType.FAQ);
                }
                else {
                    return null;
                }
            }
            else {
                Member member = memberService.findByNickname(searchWord);
                if (member == null) {
                    return null;
                }
                posts = postRepository.findByTypeAndMemberOrderByCreatedAtDesc(postType, member);
            }
        }

        for (Post p : posts) { // 스크랩 수, 공감 수
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, postType);

            p.setStarCount(allStarList.size());
        }

        return posts;
    }

    // 카테고리별 검색 (qna/faq, 제목/내용/작성자) - 페이지화o
    public Page<Post> searchQnaOrFaqByCategory(String searchType, String category, String searchWord, int page) {
        Page<Post> posts;

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        PostType postType = null;

        if (category.equals("FAQ")) {
            postType = PostType.FAQ;
        }
        else if (category.equals("QNA")) {
            postType = PostType.QNA;
        }

        if (searchType.equals("제목")) {
            posts = postRepository.findByTypeAndTitleContaining(postType, searchWord, pageable);
        } else if (searchType.equals("내용")) {
            posts = postRepository.findByTypeAndContentContaining(postType, searchWord, pageable);
        } else {
            // faq에서 '관리자' 검색 시 faq 전체 리스트 가져오면 됨
            if (postType == PostType.FAQ) {
                if (searchWord.equals("관리자")) {
                    posts = postRepository.findByTypeOrderByCreatedAtDesc(PostType.FAQ, pageable);
                }
                else {
                    return null;
                }
            }
            else {
                Member member = memberService.findByNickname(searchWord);
                if (member == null) {
                    return null;
                }
                posts = postRepository.findByTypeAndMember(postType, member, pageable);
            }
        }

        for (Post p : posts) { // 스크랩 수, 공감 수
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, postType);

            p.setStarCount(allStarList.size());
        }

        return posts;
    }

}
