package com.web.stard.service;

import com.web.stard.domain.*;
import com.web.stard.dto.request.CommPostRequestDto;
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

import java.util.List;
import java.util.Optional;

@Getter @Setter
@AllArgsConstructor
@Service
public class CommunityService {

    MemberService memberService;
    PostRepository postRepository;
    StarScrapRepository starScrapRepository;


    /* 커뮤니티 게시글 조회 (페이지화 X) */
//    public List<Post> getAllCommunityPost() {
//        List<Post> posts = postRepository.findByTypeOrderByCreatedAtDesc(PostType.COMM);
//
//        for (Post p : posts) { // 스크랩 수, 공감 수
//            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.COMM);
//            List<StarScrap> allScrapList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.SCRAP, PostType.COMM);
//
//            p.setStarCount(allStarList.size());
//            p.setScrapCount(allScrapList.size());
//        }
//
//        return posts;
//    }

    /* 커뮤니티 게시글 조회 (페이지화 추가) */
    public Page<Post> getAllCommunityPost(int page) {
//        return postRepository.findByTypeOrderByCreatedAtDesc(PostType.POST); // 페이지화 X (그냥 전체 조회)

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);
        // page -> 배열 인덱스처럼 들어가서 -1 해야 함
        // 한 페이지에 Post 10개 (개수는 추후 수정)

        Page<Post> posts = (Page<Post>) postRepository.findByType(PostType.COMM, pageable);

        for (Post p : posts) { // 스크랩 수, 공감 수
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.COMM);
            List<StarScrap> allScrapList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.SCRAP, PostType.COMM);

            p.setStarCount(allStarList.size());
            p.setScrapCount(allScrapList.size());
        }

        return posts;
    }

    public Post findById(Long id) {
        Optional<Post> result = postRepository.findByIdAndType(id, PostType.COMM);
        if (result.isPresent()) {
            Post post = result.get();

            return post;
        }
        return null;
    }

    /* 커뮤니티 게시글 세부 조회 */
    public Post getCommunityPost(Long id, String userId) {
        Optional<Post> result = postRepository.findByIdAndType(id, PostType.COMM);
        if (result.isPresent()) {
            Post post = result.get();

            if (!post.getMember().getId().equals(userId)) {
                // 작성자 != 현재 로그인 한 유저
                post.setViewCount(post.getViewCount()+1);
                postRepository.save(post);
            }

            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(post, ActType.STAR, PostType.COMM);
            List<StarScrap> allScrapList = starScrapRepository.findAllByPostAndTypeAndTableType(post, ActType.SCRAP, PostType.COMM);

            post.setStarCount(allStarList.size());
            post.setScrapCount(allScrapList.size());

            return post;
        }
        return null;
    }

    /* 전체 검색 (페이지화 X) */
    public List<Post> searchCommPost(String searchType, String searchWord) {
        List<Post> posts;

        if (searchType.equals("제목")) {
            posts = postRepository.findByTypeAndTitleContainingOrderByCreatedAtDesc(PostType.COMM, searchWord);
        } else if (searchType.equals("내용")) {
            posts = postRepository.findByTypeAndContentContainingOrderByCreatedAtDesc(PostType.COMM, searchWord);
        } else {
            Member member = memberService.findByNickname(searchWord);
            if (member == null) {
                return null;
            }
            posts = postRepository.findByTypeAndMemberOrderByCreatedAtDesc(PostType.COMM, member);
        }

        for (Post p : posts) { // 스크랩 수, 공감 수
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.COMM);
            List<StarScrap> allScrapList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.SCRAP, PostType.COMM);

            p.setStarCount(allStarList.size());
            p.setScrapCount(allScrapList.size());
        }

        return posts;
    }

    /* 전체 검색 (페이지화) */
    public Page<Post> searchCommPost(String searchType, String searchWord, int page) {
        Page<Post> posts;

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        if (searchType.equals("제목")) {
            posts = postRepository.findByTypeAndTitleContaining(PostType.COMM, searchWord, pageable);
        } else if (searchType.equals("내용")) {
            posts = postRepository.findByTypeAndContentContaining(PostType.COMM, searchWord, pageable);
        } else {
            Member member = memberService.findByNickname(searchWord);
            if (member == null) {
                return null;
            }
            posts = postRepository.findByTypeAndMember(PostType.COMM, member, pageable);
        }

        for (Post p : posts) { // 스크랩 수, 공감 수
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.COMM);
            List<StarScrap> allScrapList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.SCRAP, PostType.COMM);

            p.setStarCount(allStarList.size());
            p.setScrapCount(allScrapList.size());
        }

        return posts;
    }

    /* 카테고리 - 전체 검색 (페이지화 X) */
    public List<Post> searchCommPostByCategory(String searchType, String category, String searchWord) {
        List<Post> posts;

        if (searchType.equals("제목")) {
            posts = postRepository.findByTypeAndCategoryAndTitleContainingOrderByCreatedAtDesc(PostType.COMM, category, searchWord);
        } else if (searchType.equals("내용")) {
            posts = postRepository.findByTypeAndCategoryAndContentContainingOrderByCreatedAtDesc(PostType.COMM, category, searchWord);
        } else {
            Member member = memberService.findByNickname(searchWord);
            if (member == null) {
                return null;
            }
            posts = postRepository.findByTypeAndCategoryAndMemberOrderByCreatedAtDesc(PostType.COMM, category, member);
        }

        for (Post p : posts) { // 스크랩 수, 공감 수
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.COMM);
            List<StarScrap> allScrapList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.SCRAP, PostType.COMM);

            p.setStarCount(allStarList.size());
            p.setScrapCount(allScrapList.size());
        }

        return posts;
    }

    /* 카테고리 - 전체 검색 (페이지화) */
    public Page<Post> searchCommPostByCategory(String searchType, String category, String searchWord, int page) {
        Page<Post> posts;

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        if (searchType.equals("제목")) {
            posts = postRepository.findByTypeAndCategoryAndTitleContaining(PostType.COMM, category, searchWord, pageable);
        } else if (searchType.equals("내용")) {
            posts = postRepository.findByTypeAndCategoryAndContentContaining(PostType.COMM, category, searchWord, pageable);
        } else {
            Member member = memberService.findByNickname(searchWord);
            if (member == null) {
                return null;
            }
            posts = postRepository.findByTypeAndCategoryAndMember(PostType.COMM, category, member, pageable);
        }

        for (Post p : posts) { // 스크랩 수, 공감 수
            List<StarScrap> allStarList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.STAR, PostType.COMM);
            List<StarScrap> allScrapList = starScrapRepository.findAllByPostAndTypeAndTableType(p, ActType.SCRAP, PostType.COMM);

            p.setStarCount(allStarList.size());
            p.setScrapCount(allScrapList.size());
        }

        return posts;
    }

    /* 커뮤니티 게시글 등록 */
    public Post registerCommPost(Post post, Authentication authentication) {
        Member member = memberService.find(authentication.getName());

        post.setMember(member);
        post.setType(PostType.COMM);

        postRepository.save(post);

        return post;
    }
    
    /* 커뮤니티 게시글 수정 */
    public Post updateCommPost(Long id, CommPostRequestDto requestPost, Authentication authentication) {
        Member member = memberService.find(authentication.getName());
        Post post = findById(id);

        if (!member.getId().equals(post.getMember().getId())) {
            // 작성자랑 사용자가 다르면
            return null;
        }

        post.setTitle(requestPost.getTitle());
        post.setContent(requestPost.getContent());
        post.setCategory(requestPost.getCategory());

        postRepository.save(post);

        return post;
    }
    
    /* 커뮤니티 게시글 삭제 */
    public boolean deleteCommPost(Long id, Authentication authentication) {
        Member member = memberService.find(authentication.getName());
        Post post = findById(id);

        if (!member.getId().equals(post.getMember().getId())) {
            // 작성자랑 사용자가 다르면
            return false;
        }

        postRepository.delete(post);

        if (findById(id) == null) { // 삭제됨
            return true;
        } return false;
    }


    /* 사용자 작성한 게시글 */
    public Page<Post> findUserCommunityPost(String memberId, int page) {
        Member member = memberService.find(memberId);
        return searchCommPost("작성자", member.getNickname(), page);
    }
}
