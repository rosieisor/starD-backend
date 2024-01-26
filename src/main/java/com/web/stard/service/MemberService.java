package com.web.stard.service;

import com.web.stard.chat_stomp.ChatMessage;
import com.web.stard.chat_stomp.ChatMessageRepository;
import com.web.stard.config.jwt.JwtTokenProvider;
import com.web.stard.domain.*;
import com.web.stard.dto.ResetPasswordResponse;
import com.web.stard.repository.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Getter @Setter
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudyMemberRepository studyMemberRepository;
    private final ProfileRepository profileRepository;
    private final StudyRepository studyRepository;
    private final ApplicantRepository applicantRepository;
    private final AssigneeRepository assigneeRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final EvaluationRepository evaluationRepository;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final StudyPostRepository studyPostRepository;
    private final StarScrapRepository starScrapRepository;

    private final RedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private static final String RESET_PW_PREFIX = "ResetPwToken ";

    @Transactional
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원"));
    }

    public Member find(String id) {
        Optional<Member> result = memberRepository.findById(id);

        if(result.isPresent())
            return result.get();
        return null;
    }

    public Member findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname);
    }

    /* 비밀번호 확인 */
    public boolean checkPw(String id, String password) {
        String storedPassword = memberRepository.findPasswordById(id).getPassword(); // 사용자 pw
        if (passwordEncoder.matches(password, storedPassword)) // 입력한 비밀번호와 사용자 비밀번호 같음
            return true;
        return false;
    }

    /* 닉네임 중복 확인 */
    public boolean checkNickname(String nickname) {
        return memberRepository.existsByNickname(nickname); // true -> 있음 (사용불가)
    }

    /* 정보 수정 (닉네임, 이메일, 전화번호, 비밀번호) */
    public void updateMember(String info, String id, String information) {
        Member member = find(id);

        if (info.equals("nickname")) { // 닉네임 변경
            member.setNickname(information);
        } else if (info.equals("email")) { // 이메일 변경
            member.setEmail(information);
        } else if (info.equals("phone")) { // 전화번호 변경
            member.setPhone(information);
        } else if (info.equals("password")) { // 비밀번호 변경
            String encodedPassword = passwordEncoder.encode(information);
            member.setPassword(encodedPassword);
        }

        memberRepository.save(member);
    }

    /* 거주지 변경 */
    public void updateAddress(String id, String city, String district) {
        Member member = find(id);

        member.setCity(city);
        member.setDistrict(district);

        memberRepository.save(member);
    }

    /* 관심분야 반환 */
    public List<Interest> getInterests(String id) {
        Member member = find(id);
        return interestRepository.findAllByMember(member);
    }

    /* 관심분야 수정 */
    @Transactional
    public void updateInterest(String id, String[] interests) {
        Member member = find(id);

        // 기존 관심분야 delete 후 새로 insert
        interestRepository.deleteAllByMember(member);

        if (interests[0].equals("")) {
            return;
        }

        List<Interest> interestList = new ArrayList<>();
        for (String s : interests) {
            Interest interest = new Interest();
            interest.setMember(member);
            interest.setField(s);
            interestList.add(interest);
        }
        interestRepository.saveAll(interestList);
    }

    /* 회원 탈퇴 */
    @Transactional
    public ResponseEntity<String> deleteMember(String id, String password, Authentication authentication) {
        Member member = find(id);

        Role userRole = memberRepository.findById(authentication.getName()).get().getRoles();
        boolean forceDelete = false;

        // 강제 탈퇴인지 확인
        if (userRole == Role.ADMIN && !id.equals(authentication.getName())) {
            forceDelete = true;
            if (member.getReportCount() < 1) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("누적 신고 수가 10회 미만이면 강제 탈퇴 처리가 불가능합니다.");
            }
        }

        // 강제 탈퇴 시 비밀번호 확인 및 스터디 진행 여부와 관계 없이 탈퇴 가능
        if (!forceDelete) {
            if (!checkPw(id, password)) { // 비밀번호 틀림
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 올바르지 않습니다.");
            }

            // 스터디 진행(중단 포함) 중에는 자발적 탈퇴 불가능하게
            List<ProgressStatus> statusList = Arrays.asList(ProgressStatus.IN_PROGRESS, ProgressStatus.DISCONTINUE);
            List<StudyMember> studies = studyMemberRepository.findByMemberAndStudyProgressStatusIn(member, statusList);
            if (studies.size() > 0) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("스터디 진행 중에는 탈퇴할 수 없습니다.");
            }
        }

        // 해당 회원의 공감, 스크랩 내역 삭제
        starScrapRepository.deleteByMember(member);

        // recruiter + progressStatus가 null인 것들만 삭제 (진행으로 넘어가지 않은 모집 게시글만 삭제되게)
        List<Study> deleteStudies = studyRepository.findStudiesByRecruiterAndNullProgressStatus(member);
        studyRepository.deleteAll(deleteStudies);

        // '알 수 없음' 변경
        Member updateMember = find("admin2");

        List<Applicant> updateApplicant = applicantRepository.findByMemberAndStudyProgressStatusIsNotNull(member);
        for (Applicant applicant : updateApplicant) {
            applicant.setMember(updateMember);
            applicantRepository.save(applicant);
        }

        List<Assignee> updateAssignee = assigneeRepository.findAllByMember(member);
        for (Assignee assignee : updateAssignee) {
            Assignee newAssignee = new Assignee();
            newAssignee.setToDo(assignee.getToDo());
            newAssignee.setMember(updateMember);
            newAssignee.setToDoStatus(assignee.isToDoStatus());

            assigneeRepository.delete(assignee);
            assigneeRepository.save(newAssignee);
        }

        List<ChatMessage> updateChatMessage = chatMessageRepository.findByMember(member);
        for (ChatMessage chatMessage : updateChatMessage) {
            chatMessage.setMember(updateMember);
            chatMessageRepository.save(chatMessage);
        }

        List<Evaluation> updateEvaluation = evaluationRepository.findByTargetOrderByStudyActivityDeadlineDesc(member); // 내가 target
        List<Evaluation> updateEvaluation2 = evaluationRepository.findByMemberOrderByStudyActivityDeadlineDesc(member); // 내가 member
        for (Evaluation evaluation : updateEvaluation) {
            evaluation.setTarget(updateMember);
            evaluationRepository.save(evaluation);
        }
        for (Evaluation evaluation : updateEvaluation2) {
            evaluation.setMember(updateMember);
            evaluationRepository.save(evaluation);
        }

        List<Post> updatePost = postRepository.findAllByMember(member);
        for (Post post : updatePost) {
            post.setMember(updateMember);
            postRepository.save(post);
        }

        List<Reply> updateReply = replyRepository.findAllByMember(member);
        for (Reply reply : updateReply) {
            reply.setMember(updateMember);
            replyRepository.save(reply);
        }

        List<Study> updateStudy = studyRepository.findByRecruiterAndProgressStatus(member, ProgressStatus.WRAP_UP);
        for (Study study : updateStudy) {
            study.setRecruiter(updateMember);
            studyRepository.save(study);
        }

        List<StudyMember> updateStudyMember = studyMemberRepository.findByMemberAndStudyProgressStatusOrderByIdDesc(member, ProgressStatus.WRAP_UP);
        for (StudyMember studyMember : updateStudyMember) {
            studyMember.setMember(updateMember);
            studyMemberRepository.save(studyMember);
        }

        List<StudyPost> updateStudyPost = studyPostRepository.findByMemberAndStudyProgressStatus(member, ProgressStatus.WRAP_UP);
        for (StudyPost studyPost : updateStudyPost) {
            studyPost.setMember(updateMember);
            studyPostRepository.save(studyPost);
        }

        interestRepository.deleteAllByMember(member);

        memberRepository.delete(member);

        // 프로필 삭제
        profileRepository.deleteById(member.getProfile().getId());


        if (find(id) == null) { // 삭제됨
            return ResponseEntity.ok("탈퇴 성공");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("탈퇴 실패");
    }

    public List<Member> findId(String email, String phone) {
        return memberRepository.findByEmailAndPhone(email, phone);
    }


    // authentication으로 닉네임 찾기
    public Member findNickNameByAuthentication(Authentication authentication) {
        return memberRepository.findNicknameById(authentication.getName());
    }

    public ResetPasswordResponse resetPassword(HttpServletRequest request, String token) throws Exception {
        String email = validateResetPwToken(token);

        String accessToken = jwtTokenProvider.createToken(email);

        // TODO 로그 삭제
        log.info("비밀번호 재설정 반환 uri: " + request.getRequestURI() + "?token=" + token);

        return ResetPasswordResponse.builder()
                .uri(request.getRequestURI() + "?token=" + token)
                .email(email)
                .accessToken(accessToken).build();
    }

    private String validateResetPwToken(String token) throws Exception {
        String email = (String)redisTemplate.opsForValue().get(RESET_PW_PREFIX + token);

        if (email == null)
            throw new Exception();

        return email;
    }
}
