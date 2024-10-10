package com.web.stard.domain.member.api;

import com.web.stard.domain.board.community.application.CommunityService;
import com.web.stard.domain.board.global.application.ReplyService;
import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.Reply;
import com.web.stard.domain.board.study.application.EvaluationService;
import com.web.stard.domain.board.study.application.StudyPostService;
import com.web.stard.domain.board.study.application.StudyService;
import com.web.stard.domain.board.study.domain.*;
import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.application.ProfileService;
import com.web.stard.domain.member.domain.Interest;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.Profile;
import com.web.stard.domain.member.dto.PasswordUpdateDto;
import com.web.stard.domain.member.dto.ProfileResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Getter @Setter
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/mypage")
public class MyPageController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    private final ProfileService profileService;
    private final StudyService studyService;
    private final CommunityService communityService;

    private final EvaluationService evaluationService;
    private final ReplyService replyService;
    private final StudyPostService studyPostService;

    /* 정보 반환 */
    @GetMapping("/update")
    public Member getMember() {
        Member member = null;
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String id = authentication.getName(); // 사용자 아이디
            if (id.equals("anonymousUser")) {
                return null;
            }
            member = memberService.find(id);
        }
        return member;
    }

    /* 관심분야 반환 */
    @GetMapping("/update/interests")
    public List<Interest> getInterests() {
        List<Interest> interests = null;
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String id = authentication.getName(); // 사용자 아이디
            if (id.equals("anonymousUser")) {
                return null;
            }
            interests = memberService.getInterests(id);
        }
        System.out.println("관심분야 : " + interests);
        return interests;
    }

    /* 닉네임 중복 확인 */
    @PostMapping("/check/nickname")
    public boolean checkNickname(@RequestParam(name = "nickname") String nickname) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String id = authentication.getName(); // 사용자 아이디
            if (!id.equals("anonymousUser")) {
                String currentNickname = memberService.find(id).getNickname();
                if (nickname.equals(currentNickname)) { // 현재 닉네임 == 바꿀 닉네임이면 사용 가능하게
                    return false;
                }
            }
        }
        return memberService.checkNickname(nickname);
        // true -> 이미 존재 (사용 불가), false -> 없음 (사용 가능)
    }

    /* 비밀번호 확인 (필요한 경우 사용) */
    @PostMapping("/check/password")
    public boolean checkPassword(@RequestParam(name = "password") String password) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String id = authentication.getName(); // 사용자 아이디
            if (id.equals("anonymousUser")) {
                return false;
            }
            return memberService.checkPw(id, password); // true -> 맞음 / false -> 틀림
        }
        return false;
    }

    /* 닉네임 변경 */
    @PostMapping("/update/nickname")
    public ResponseEntity<String> updateNickname(@RequestParam(name = "nickname") String nickname) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String id = authentication.getName(); // 사용자 아이디
            if (!id.equals("anonymousUser")) {
                memberService.updateMember("nickname", id, nickname);
                return ResponseEntity.ok(nickname);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 사용자 인증입니다.");
    }

    /* 이메일 변경 */
    @PostMapping("/update/email")
    public ResponseEntity<String> updateEmail(@RequestParam(name = "email") String email) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String id = authentication.getName(); // 사용자 아이디
            if (!id.equals("anonymousUser")) {
                memberService.updateMember("email", id, email);
                return ResponseEntity.ok(email);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 사용자 인증입니다.");
    }

    /* 전화번호 변경 */
    @PostMapping("/update/phone")
    public ResponseEntity<String> updatePhone(@RequestParam(name = "phone") String phone) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String id = authentication.getName(); // 사용자 아이디
            if (!id.equals("anonymousUser")) {
                memberService.updateMember("phone", id, phone);
                return ResponseEntity.ok(phone);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 사용자 인증입니다.");
    }

    /* 비밀번호 변경 */
    @PostMapping("/update/password")
    public ResponseEntity<String> updatePassword(@RequestParam(name = "password") String password,
                                                 @RequestParam(name = "newPassword") String newPassword) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String id = authentication.getName(); // 사용자 아이디
            if (!id.equals("anonymousUser")) {
                // 패스워드 일치 여부 확인
                if (!memberService.checkPw(id, password)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
                }

                memberService.updateMember("password", id, newPassword);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 사용자 인증입니다.");
    }

    /* 거주지 변경 */
    @PostMapping("/update/address")
    public ResponseEntity<String> updateAddress(@RequestParam(name = "city") String city,
                                                @RequestParam(name = "district") String district) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String id = authentication.getName(); // 사용자 아이디
            if (!id.equals("anonymousUser")) {
                memberService.updateAddress(id, city, district);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 사용자 인증입니다.");
    }

    /* 관심분야 변경 */
    @PostMapping("/update/interest")
    public ResponseEntity<String> updateInterest(@RequestParam(name = "interestList") String interests) {
        System.out.println("관심분야 : " + interests);

        String[] interestArray = interests.split(",");

        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String id = authentication.getName(); // 사용자 아이디
            if (!id.equals("anonymousUser")) {
                memberService.updateInterest(id, interestArray);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 사용자 인증입니다.");
    }

    /* 회원 탈퇴 (아직 기능 X) */
    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam(name = "password") String password, Authentication authentication) {
        return memberService.deleteMember(authentication.getName(), password, authentication);
    }

    // [U] 프로필 수정
    @PutMapping("/profile")
    public Profile updateProfile(Authentication authentication, String introduce, MultipartFile imgFile) {
        return profileService.updateProfile(authentication.getName(), introduce, imgFile);
    }

    // [D] 프로필 삭제
    @DeleteMapping("/profile")
    public void deleteProfile(Authentication authentication) {
        profileService.deleteProfile(authentication.getName());
    }

    // [R] 프로필 조회
    @GetMapping("/profile")
    public Profile getProfile(Authentication authentication) {
        return profileService.getProfile(authentication.getName());
    }

    // [R] 프로필 이미지 조회
    @GetMapping("/profile/image/{imageUrl}")
    public ResponseEntity<Resource> getImage(@PathVariable(name = "imageUrl") String imageUrl) throws IOException {
        return profileService.getProfileImage(imageUrl);
    }

    /* 다른 사용자 프로필 조회 */
    @GetMapping("/profile/{memberId}")
    public ProfileResponse getUserProfile(@PathVariable(name = "memberId") String memberId) {
        return profileService.getUserProfile(memberId);
    }

    /* 다른 사용자 프로필 조회 - 스터디 모집 게시글 */
    @GetMapping("/profile/{memberId}/open-study")
    public Page<Study> findUserOpenHistory(@PathVariable(name = "memberId") String memberId,
                                           @RequestParam(name = "page", defaultValue = "1", required = false) int page) {
        return studyService.findByRecruiter(memberId, page);
    }

    /* 다른 사용자 프로필 조회 - 커뮤니티 게시글 */
    @GetMapping("/profile/{memberId}/community")
    public Page<Post> findUserCommunityPost(@PathVariable(name = "memberId") String memberId,
                                            @RequestParam(name = "page", defaultValue = "1", required = false) int page) {
        return communityService.findUserCommunityPost(memberId, page);
    }

    // [U] 개인 신뢰도 수정
    @PutMapping("/credibility")
    public Profile updateCredibility(Authentication authentication) {
        return profileService.updateCredibility(authentication.getName());
    }

    // [R] 개인 신뢰도 조회
    @GetMapping("/credibility")
    public float getCredibility(Authentication authentication) {
        return profileService.getCredibility(authentication.getName());
    }

    // [R] 스터디 신청 내역
    @GetMapping("/apply-study")
    public Page<Applicant> findApplyHistory(@RequestParam(name = "page", defaultValue = "1", required = false) int page, Authentication authentication) {
        System.out.println("스터디 신청 내역 함수 진입");
        return studyService.findByMember(authentication, page);
    }

    // [R] 스터디 개설 내역
    @GetMapping("/open-study")
    public Page<Study> findOpenHistory(@RequestParam(name = "page", defaultValue = "1", required = false) int page, Authentication authentication) {
        return studyService.findByRecruiter(authentication.getName(), page);
    }

    // [R] 스터디 참여 내역
    @GetMapping("/studying")
    public Page<StudyMember> findStudyingHistory(@RequestParam(name = "page", defaultValue = "1", required = false) int page, Authentication authentication) {
        System.out.println("스터디 참여 내역 컨트롤러 진입 O ");
        return studyService.findStudying(authentication, page);
    }


    /* 진행 완료된 스터디 리스트 조회 */
    @GetMapping("/wrap-up-study")
    public List<StudyMember> findWrapUpStudy(Authentication authentication) {
        return studyService.findWrapUpStudy(authentication);
    }

    /* 평가 당한 내역 리스트 전체 조회 (전체 스터디) */
    @GetMapping("/rate/target")
    public List<Evaluation> getMyEvaluationList(Authentication authentication) {
        return evaluationService.getMyEvaluationList(authentication.getName());
    }

    /* 평가 당한 내역 리스트 조회 (스터디 별로) */
    @GetMapping("/rate/target/study/{studyId}")
    public List<Evaluation> getMyEvaluationListByStudy(@PathVariable(name = "studyid") Long studyId, Authentication authentication) {
        return evaluationService.getMyEvaluationListByStudy(studyId, authentication);
    }

    /* 평가 당한 내역 상세 조회 */
    @GetMapping("/rate/target/{evaluationId}")
    public Evaluation getMyEvaluation(@PathVariable(name = "evaluationId") Long evaluationId, Authentication authentication) {
        return evaluationService.getMyEvaluation(evaluationId, authentication);
    }

    /* 내가 작성한 글 조회 */
    @GetMapping("/post")
    public Page<Post> findMyPost(@RequestParam(name = "page", defaultValue = "1", required = false) int page, Authentication authentication) {
        return communityService.findPostByMember(authentication.getName(), page);
    }

    /* 내가 작성한 STUDYPOST 글 조회 */
    @GetMapping("/studypost")
    public Page<StudyPost> findMyStudyPost(@RequestParam(name = "page", defaultValue = "1", required = false) int page, Authentication authentication) {
        return studyPostService.findByMember(authentication.getName(), page);
    }

    /* 내가 작성한 댓글 조회 */
    @GetMapping("/reply")
    public Page<Reply> findMyReply(@RequestParam(name = "page", defaultValue = "1", required = false) int page, Authentication authentication) {
        return replyService.findByMember(authentication.getName(), page);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordUpdateDto passwordUpdateDto, Authentication authentication) {
        memberService.resetPassword(passwordUpdateDto, authentication);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
