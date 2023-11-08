//package com.web.stard.service;
//
//import com.web.stard.domain.Member;
//import com.web.stard.domain.Post;
//import com.web.stard.domain.PostType;
//import com.web.stard.domain.Role;
//import com.web.stard.repository.PostRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.Assert.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@Transactional
//class NoticeServiceTest {
//
//    /**
//     * WebSecurityConfig에서 .antMatchers("/notice**").hasRole("ADMIN") 활성화하고 테스트 진행하기
//     */
//
//    @Autowired
//    private MemberService memberService;
//    @Autowired
//    private PostRepository postRepository;
//    @Autowired
//    private NoticeService noticeService;
//
//    @Rollback(false)
//    @Test
//    @WithMockUser(authorities = "ADMIN")
//    void notice_등록_관리자() {
//        //given
//        Member adminMember = memberService.find("testAdmin");
//        Role userRole = adminMember.getRoles();
//
//        // 현재 사용자의 권한 이름을 SimpleGrantedAuthority 객체로 감싸서 리스트에 담긴다.
//        Authentication authentication = new UsernamePasswordAuthenticationToken(adminMember.getId(), null);
//
//        Post post = new Post();
//        post.setTitle("Notice Title");
//        post.setContent("Notice Content");
//
//        //when
//        noticeService.createNotice(post, authentication);
//
//        //then
//        List<Post> savedPosts = postRepository.findAll();
//        assertEquals(1, savedPosts.size()); // 생성된 글이 1개인지 확인
//
//        Post savedPost = savedPosts.get(0);
//        assertEquals(savedPost.getId(), savedPost.getId()); // 생성된 글의 아이디와 검색된 게시글의 아이디가 같은지 확인
//        assertEquals(PostType.NOTICE, savedPost.getType()); // 생성된 글의 타입이 NOTICE인지 확인
//        assertEquals(adminMember.getId(), savedPost.getMember().getId()); // 생성된 글의 작성자가 맞는지 확인
//        assertThat(userRole).isEqualTo(Role.ADMIN); // 작성자가 ADMIN인지 확인
//    }
//
//    @Rollback(false)
//    @Test
//    @WithMockUser(authorities = "USER")
//    void notice_등록_사용자_예외() {
//        //given
//        Member member = new Member();
//        member.setId("testUser");
//        Role role = Role.USER;
//        member.setRoles(role);
//        memberService.saveMember(member);
//
//        Authentication userAuth = new UsernamePasswordAuthenticationToken(member.getId(), null);
//
//        Post post = new Post();
//        post.setTitle("Notice Title");
//        post.setContent("Notice Content");
//
//        //when
//        noticeService.createNotice(post, userAuth);
//
//        //then
//        List<Post> savedPosts = postRepository.findAll();
//        assertEquals(1, savedPosts.size()); // 생성된 글이 1개인지 확인
//
//        Post savedPost = savedPosts.get(0);
//        assertEquals(savedPost.getId(), savedPost.getId()); // 생성된 글의 아이디와 검색된 게시글의 아이디가 같은지 확인
//        assertEquals(PostType.NOTICE, savedPost.getType()); // 생성된 글의 타입이 NOTICE인지 확인
//        assertEquals(member.getId(), savedPost.getMember().getId()); // 생성된 글의 작성자가 맞는지 확인
//        assertThat(savedPosts.get(0).getMember().getRoles()).isEqualTo(Role.ADMIN); // 작성자가 ADMIN인지 확인 -> 실패
//    }
//
//    @Rollback(false)
//    @Test
//    void notice_리스트조회_관리자() {
//        //given
//        Member adminMember = memberService.find("testAdmin");
//
//        Authentication adminAuth = new UsernamePasswordAuthenticationToken(adminMember.getId(), null);
//
//        Post post1 = new Post();
//        post1.setTitle("Notice Title1");
//        post1.setContent("Notice Content1");
//        noticeService.createNotice(post1, adminAuth);
//
//        Post post2 = new Post();
//        post2.setTitle("Notice Title2");
//        post2.setContent("Notice Content2");
//        noticeService.createNotice(post2, adminAuth);
//
//        //when
//        List<Post> postList = noticeService.getAllNotice(1); // 비회원도 조회 가능(권한 체크x)
//
//        //then
//        assertEquals(2, postList.size()); // 생성된 글이 2개인지 확인
//        assertEquals(post2.getTitle(), postList.get(0).getTitle()); // 최근 순으로 조회되는지 확인
//    }
//
//    @Rollback(false)
//    @Test
//    void notice_리스트조회_사용자() {
//        //given
//        Member adminMember = memberService.find("testAdmin");
//
//        Member member = new Member();
//        member.setId("testUser");
//        Role role = Role.USER;
//        member.setRoles(role);
//        memberService.saveMember(member);
//
//        Authentication adminAuth = new UsernamePasswordAuthenticationToken(adminMember.getId(), null);
//        // member 로그인 상태로 변경
//        Authentication userAuth = new UsernamePasswordAuthenticationToken(member.getId(), null);
//
//        Post post1 = new Post();
//        post1.setTitle("Notice Title1");
//        post1.setContent("Notice Content1");
//        noticeService.createNotice(post1, adminAuth);
//
//        Post post2 = new Post();
//        post2.setTitle("Notice Title2");
//        post2.setContent("Notice Content2");
//        noticeService.createNotice(post2, adminAuth);
//
//        //when
//        List<Post> postList = noticeService.getAllNotice(1); // 비회원도 조회 가능(권한 체크x)
//
//        //then
//        assertEquals(2, postList.size()); // 생성된 글이 2개인지 확인
//        assertEquals(post2.getTitle(), postList.get(0).getTitle()); // 최근 순으로 조회되는지 확인
//    }
//    @Rollback(false)
//    @Test
//    void notice_리스트조회_비회원() {
//        //given
//        Member adminMember = memberService.find("testAdmin");
//
//        Authentication adminAuth = new UsernamePasswordAuthenticationToken(adminMember.getId(), null);
//
//        // 익명 사용자로 로그인 상태 설정(비회원)
//        Authentication anonymousAuth = new AnonymousAuthenticationToken(
//                "anonymousUser", // principal
//                "anonymousUser", // key
//                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS") // authorities
//        );
//        SecurityContextHolder.getContext().setAuthentication(anonymousAuth);
//
//        Post post1 = new Post();
//        post1.setTitle("Notice Title1");
//        post1.setContent("Notice Content1");
//        noticeService.createNotice(post1, adminAuth);
//
//        Post post2 = new Post();
//        post2.setTitle("Notice Title2");
//        post2.setContent("Notice Content2");
//        noticeService.createNotice(post2, adminAuth);
//
//        //when
//        List<Post> postList = noticeService.getAllNotice(1); // 비회원도 조회 가능(권한 체크x)
//
//        //then
//        assertEquals(2, postList.size()); // 생성된 글이 2개인지 확인
//        assertEquals(post2.getTitle(), postList.get(0).getTitle()); // 최근 순으로 조회되는지 확인
//    }
//
//    @WithMockUser(authorities = "ADMIN")
//    @Rollback(false)
//    @Test
//    void notice_상세조회_관리자() {
//        //given
//        Member adminMember = memberService.find("testAdmin");
//
//        Authentication adminAuth = new UsernamePasswordAuthenticationToken(adminMember.getId(), null);
//
//        Post post1 = new Post();
//        post1.setTitle("Notice Title1");
//        post1.setContent("Notice Content1");
//        noticeService.createNotice(post1, adminAuth);
//
//        //when
//        Post detailPost = noticeService.getNoticeDetail(post1.getId());
//
//        //then
//        assertEquals(post1.getId(), detailPost.getId()); // id가 맞는지 확인
//        assertEquals(post1.getTitle(), detailPost.getTitle()); // 제목이 맞는지 확인
//        assertEquals(post1.getContent(), detailPost.getContent()); // 내용이 맞는지 확인
//    }
//
//    @WithMockUser(authorities = "USER")
//    @Rollback(false)
//    @Test
//    void notice_상세조회_사용자() {
//        //given
//        Member adminMember = memberService.find("testAdmin");
//
//        Member member = new Member();
//        member.setId("testUser");
//        Role role = Role.USER;
//        member.setRoles(role);
//        memberService.saveMember(member);
//
//        Authentication adminAuth = new UsernamePasswordAuthenticationToken(adminMember.getId(), null);
//        // member 로그인 상태로 변경
//        Authentication userAuth = new UsernamePasswordAuthenticationToken(member.getId(), null);
//
//        Post post1 = new Post();
//        post1.setTitle("Notice Title1");
//        post1.setContent("Notice Content1");
//        noticeService.createNotice(post1, adminAuth);
//
//        //when
//        Post detailPost = noticeService.getNoticeDetail(post1.getId());
//
//        //then
//        assertEquals(post1.getId(), detailPost.getId()); // id가 맞는지 확인
//        assertEquals(post1.getTitle(), detailPost.getTitle()); // 제목이 맞는지 확인
//        assertEquals(post1.getContent(), detailPost.getContent()); // 내용이 맞는지 확인
//    }
//
//    @WithMockUser(authorities = "ADMIN")
//    @Rollback(false)
//    @Test
//    void notice_수정_관리자() {
//        //given
//        Member adminMember = memberService.find("testAdmin");
//        Member adminMember2 = memberService.find("admin");  // 둘 다 db에 있어야 테스트 가능(직접 생성 or 회원가입 후 authority_id를 2로 바꿔주기)
//
//        Authentication adminAuth = new UsernamePasswordAuthenticationToken(adminMember.getId(), null);
//        //Authentication adminAuth2 = new UsernamePasswordAuthenticationToken(adminMember2.getId(), null);
//
//        Post post = new Post();
//        post.setTitle("Notice Title");
//        post.setContent("Notice Content");
//        post.setMember(adminMember);
//        //post.setMember(adminMember2);
//        noticeService.createNotice(post, adminAuth);
//
//        //when
//        post.setTitle("Updated Notice Title");
//        post.setContent("Updated Notice Content");
//        noticeService.updateNotice(post.getId(), post, adminAuth);  // 1. 관리자이자 작성자 본인이 수정
//        //noticeService.updateNotice(post.getId(), post, adminAuth2); // 2. 다른 관리자가 수정
//
//        //then
//        List<Post> savedPosts = postRepository.findAll();
//        assertEquals(1, savedPosts.size()); // 저장된 글이 1개인지 확인
//
//        Post updatedPost = savedPosts.get(0);
//        assertEquals(post.getId(), updatedPost.getId()); // 수정된 글의 아이디와 검색된 게시글의 아이디가 같은지 확인
//        assertEquals(PostType.NOTICE, updatedPost.getType()); // 수정된 글의 타입이 NOTICE인지 확인
//        assertEquals(post.getTitle(), updatedPost.getTitle()); // 제목이 맞는지 확인
//        assertEquals(post.getContent(), updatedPost.getContent()); // 내용이 맞는지 확인
//        assertThat(savedPosts.get(0).getMember().getRoles()).isEqualTo(Role.ADMIN); // 작성자가 ADMIN인지 확인
//    }
//
//    @WithMockUser(authorities = "USER")
//    @Rollback(false)
//    @Test
//    void notice_수정_사용자_예외() {
//        //given
//        Member adminMember = memberService.find("testAdmin");
//
//        Member member = new Member();
//        member.setId("testUser");
//        Role role = Role.USER;
//        member.setRoles(role);
//        memberService.saveMember(member);
//
//        Authentication adminAuth = new UsernamePasswordAuthenticationToken(adminMember.getId(), null);
//
//
//        Post post = new Post();
//        post.setTitle("Notice Title");
//        post.setContent("Notice Content");
//        noticeService.createNotice(post, adminAuth);
//
//        Authentication userAuth = new UsernamePasswordAuthenticationToken(member.getId(), null);
//
//        //when
//        post.setTitle("Updated Notice Title");
//        post.setContent("Updated Notice Content");
//        //post.setMember(member);
//        noticeService.updateNotice(post.getId(), post, userAuth);
//
//        //then
//        List<Post> savedPosts = postRepository.findAll();
//        assertEquals(1, savedPosts.size()); // 저장된 글이 1개인지 확인
//
//        Post updatedPost = savedPosts.get(0);
//        assertEquals(post.getId(), updatedPost.getId()); // 수정된 글의 아이디와 검색된 게시글의 아이디가 같은지 확인
//        assertEquals(PostType.NOTICE, updatedPost.getType()); // 수정된 글의 타입이 NOTICE인지 확인
//        assertEquals(post.getTitle(), updatedPost.getTitle()); // 제목이 맞는지 확인
//        assertEquals(post.getContent(), updatedPost.getContent()); // 내용이 맞는지 확인
//        assertThat(savedPosts.get(0).getMember().getRoles()).isEqualTo(Role.ADMIN); // 작성자가 ADMIN인지 확인 -> 실패
//    }
//
//    @WithMockUser(authorities = "ADMIN")
//    @Rollback(false)
//    @Test
//    void notice_삭제_관리자() {
//        //given
//        Member adminMember = memberService.find("testAdmin");
//        Member adminMember2 = memberService.find("admin");
//
//        // adminAuth가 로그인했다고 가정
//        Authentication adminAuth = new UsernamePasswordAuthenticationToken(adminMember.getId(), null);
//
//
//        Post post = new Post();
//        post.setTitle("Notice Title2");
//        post.setContent("Notice Content2");
//        noticeService.createNotice(post, adminAuth);
//        Long postId = post.getId();
//
//        // adminAuth2 로그인 상태로 변경
//        Authentication adminAuth2 = new UsernamePasswordAuthenticationToken(adminMember2.getId(), null);
//
//        //when
//        noticeService.deleteNotice(postId, adminAuth2);
//
//        //then
//        Optional<Post> deletedPost = postRepository.findById(postId);
//        assertFalse(deletedPost.isPresent());   // 삭제한 글이 존재하는지 확인
//    }
//
//    @WithMockUser(authorities = "USER")
//    @Rollback(false)
//    @Test
//    void notice_삭제_사용자_예외() {
//        //given
//        Member adminMember = memberService.find("testAdmin");
//
//        Member member = new Member();
//        member.setId("testUser");
//        Role role = Role.USER;
//        member.setRoles(role);
//        memberService.saveMember(member);
//
//        Authentication adminAuth = new UsernamePasswordAuthenticationToken(adminMember.getId(), null);
//        Authentication userAuth = new UsernamePasswordAuthenticationToken(member.getId(), null);
//
//
//        Post post = new Post();
//        post.setTitle("Notice Title2");
//        post.setContent("Notice Content2");
//        noticeService.createNotice(post, adminAuth);
//        Long postId = post.getId();
//
//        //when
//        noticeService.deleteNotice(postId, userAuth);
//
//        //then
//        Optional<Post> deletedPost = postRepository.findById(postId);
//        assertFalse(deletedPost.isPresent());   // 삭제한 글이 존재하는지 확인 -> 실패
//    }
//
//}