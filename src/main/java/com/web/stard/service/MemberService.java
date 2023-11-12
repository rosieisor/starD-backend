package com.web.stard.service;

import com.web.stard.chat_stomp.ChatMessage;
import com.web.stard.chat_stomp.ChatMessageRepository;
import com.web.stard.domain.*;
import com.web.stard.repository.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Getter @Setter
@AllArgsConstructor
public class MemberService {

    MemberRepository memberRepository;
    InterestRepository interestRepository;
    PasswordEncoder passwordEncoder;
    StudyMemberRepository studyMemberRepository;
    ProfileRepository profileRepository;
    StudyRepository studyRepository;
    ApplicantRepository applicantRepository;
    AssigneeRepository assigneeRepository;
    ChatMessageRepository chatMessageRepository;
    EvaluationRepository evaluationRepository;
    PostRepository postRepository;
    ReplyRepository replyRepository;
    StudyPostRepository studyPostRepository;


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
    public ResponseEntity<String> deleteMember(String id, String password) {
        Member member = find(id);

        if (!checkPw(id, password)) { // 비밀번호 틀림
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 올바르지 않습니다.");
        }

        // 스터디 진행(중단 포함) 중에는 자발적 탈퇴 불가능하게
        List<ProgressStatus> statusList = Arrays.asList(ProgressStatus.IN_PROGRESS, ProgressStatus.DISCONTINUE);
        List<StudyMember> studies = studyMemberRepository.findByMemberAndStudyProgressStatusIn(member, statusList);
        if (studies.size() > 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("스터디 진행 중에는 탈퇴할 수 없습니다.");
        }

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

        List<StudyMember> updateStudyMember = studyMemberRepository.findByMemberAndStudyProgressStatus(member, ProgressStatus.WRAP_UP);
        for (StudyMember studyMember : updateStudyMember) {
            studyMember.setMember(updateMember);
            studyMemberRepository.save(studyMember);
        }

        List<StudyPost> updateStudyPost = studyPostRepository.findByMemberAndStudyProgressStatus(member, ProgressStatus.WRAP_UP);
        for (StudyPost studyPost : updateStudyPost) {
            studyPost.setMember(updateMember);
            studyPostRepository.save(studyPost);
        }

        memberRepository.delete(member);

        // 프로필 삭제
        profileRepository.deleteById(member.getProfile().getId());


        if (find(id) == null) { // 삭제됨
            return ResponseEntity.ok("탈퇴 성공");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("탈퇴 실패");
    }

    public Member findId(String email, String phone) {
        return memberRepository.findByEmailAndPhone(email, phone);
    }


    // authentication으로 닉네임 찾기
    public Member findNickNameByAuthentication(Authentication authentication) {
        return memberRepository.findNicknameById(authentication.getName());
    }
}
