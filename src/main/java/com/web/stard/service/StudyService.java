package com.web.stard.service;


import com.web.stard.domain.*;
import com.web.stard.dto.StudyDto;
import com.web.stard.dto.response.Top5Dto;
import com.web.stard.repository.ApplicantRepository;
import com.web.stard.repository.StudyMemberRepository;
import com.web.stard.repository.StudyRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final MemberService memberService;
    private final ApplicantRepository applicantRepository;
    private final StudyMemberRepository studyMemberRepository;


    public Study findById(Long id) {
        Optional<Study> result = studyRepository.findById(id);

        if (result.isEmpty())
            return null;
        return result.get();

    }

    @Transactional
    public Page<Study> findByRecruiter(Authentication authentication, int page) {

        String userId = authentication.getName();
        Member member = memberService.find(userId);

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 9, sort);

        Page<Study> studies = studyRepository.findByRecruiter(member, pageable);

        return studies;
    }

    @Transactional
    public Page<Applicant> findByMember(Authentication authentication, int page) {

        String userId = authentication.getName();
        Member member = memberService.find(userId);

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(page - 1, 9, sort);

        Page<Applicant> applicants = applicantRepository.findByMember(member, pageable);

        return applicants;
    }

    public Page<Study> findAllByOrderByRecruitStatus(int page) {

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 9, sort);

        Page<Study> studies = studyRepository.findAllByOrderByRecruitStatus(pageable);

        return studies;
    }

    public Page<Study> findByTitleContainingOrderByRecruitStatus(String keyword, int page) {

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 9, sort);

        Page<Study> studies = studyRepository.findByTitleContainingOrderByRecruitStatus(keyword, pageable);

        return studies;
    }

    public Page<Study> findByRecruiter_NicknameContainingOrderByRecruitStatus(String keyword, int page) {

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<Study> studies = studyRepository.findByRecruiter_NicknameContainingOrderByRecruitStatus(keyword, pageable);

        return studies;


    }

    public Page<Study> findByContentContainingOrderByRecruitStatus(String keyword, int page) {
        System.out.println("내용 키워드 : " + keyword);

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<Study> studies = studyRepository.findByContentContainingOrderByRecruitStatus(keyword, pageable);

        return studies;
    }

    public Page<Study> findByTitleContainingAndRecruitStatus(String keyword, RecruitStatus recruitStatus, int page) {

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<Study> studies = studyRepository.findByTitleContainingAndRecruitStatus(keyword, recruitStatus, pageable);

        return studies;
    }

    public Page<Study> findByContentContainingAndRecruitStatus(String keyword, RecruitStatus recruitStatus, int page) {

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<Study> studies = studyRepository.findByContentContainingAndRecruitStatus(keyword, recruitStatus, pageable);

        return studies;
    }

    public Page<Study> findByRecruiterContainingAndRecruitStatus(String keyword, RecruitStatus recruitStatus, int page) {

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<Study> studies = studyRepository.findByRecruiterContainingAndRecruitStatus(keyword, recruitStatus, pageable);

        return studies;
    }

    public Page<Study> findByRecruitStatus(RecruitStatus recruitStatus, int page) {

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<Study> studies = studyRepository.findByRecruitStatus(recruitStatus, pageable);

        return studies;
    }

    public Page<StudyMember> findStudying(Authentication authentication, int page) {
        System.out.println("findStudying 진입 O ");
        String userId = authentication.getName();
        Member member = memberService.find(userId);

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(page - 1, 9, sort);

        Page<StudyMember> studies = studyMemberRepository.findByMember(member, pageable);
        System.out.println(studies.getTotalElements());
        return studies;
    }

    /* 사용자별 진행 완료된 스터디 조회 */
    public List<StudyMember> findWrapUpStudy(Authentication authentication) {
        Member member = memberService.find(authentication.getName());

        return studyMemberRepository.findByMemberAndStudyProgressStatusOrderByIdDesc(member, ProgressStatus.WRAP_UP);
    }

    @Transactional
    public boolean deleteStudy(long id, Authentication authentication) {

        Study study = findById(id);

        // TODO 삭제할 스터디 게시글이 개설 전, 후 상태 확인하기
        // 삭제할 스터디 게시글이 개설 후라면 삭제 불가
        if (study.getProgressStatus() != null)
            return false;

        // TODO 삭제할 스터디 게시글의 작성자가 로그인된 사용자의 아이디와 같은지 체크
         String userId = authentication.getName();

        // 로그인된 사용자가 아니거나, 로그인된 사용자와 게시글 작성자가 다르거나
        // 해당 id를 가진 게시글이 없을 경우 null 반환
        if(userId == null || study == null || !study.getRecruiter().getId().equals(userId))     // TODO 추후에 주석 제거 필요
            return false;

        studyRepository.delete(study);

        if (findById(id) == null) {
            return true;
        } return false;
    }

    @Transactional
    public Study createStudy(StudyDto studyDto, Authentication authentication) {

        String userId = authentication.getName();

        Member member = memberService.find(userId);

        Study result = Study.builder()
                .title(studyDto.getTitle())
                .content(studyDto.getContent())
                .capacity(studyDto.getCapacity())
                .recruiter(member)
                .city(studyDto.getCity())
                .district(studyDto.getDistrict())
                .tags(studyDto.getTags())
                .onOff(studyDto.getOnOff())
                .activityStart(studyDto.getActivityStart())
                .activityDeadline(studyDto.getActivityDeadline())
                .recruitmentDeadline(studyDto.getRecruitmentDeadline())
                .viewCount(0)
                .progressStatus(null)
                .field(studyDto.getField())
                .recruitStatus(RecruitStatus.valueOf("RECRUITING"))
                .type(PostType.STUDY)
                .build();

        studyRepository.save(result);

        return result;
    }

    @Transactional
    public Study updateStudy(long id, StudyDto studyDto, Authentication authentication) {

//        String userId = authentication.getName();     // TODO 추후에 주석 제거 필요
//        if(userId == null)
//            return null;
        Study result = findById(id);

        // 로그인된 사용자가 아니거나, 로그인된 사용자와 게시글 작성자가 다르거나
        // 해당 id를 가진 게시글이 없을 경우 null 반환
//        if(userId == null || result == null || !result.getRecruiter().equals(memberRepository.findNicknameById(userId)))     // TODO 추후에 주석 제거 필요
//            return null;

        RecruitStatus recruitStatus = result.getRecruitStatus();

        if (!recruitStatus.toString().equals("RECRUITING")) {     // TODO 스터디 모집 상태가 모집 중일 때만 수정 가능
            return null;
        }

        result.setTitle(studyDto.getTitle());
        result.setContent(studyDto.getContent());
        result.setCity(studyDto.getCity());
        result.setDistrict(studyDto.getDistrict());
        result.setTags(studyDto.getTags());
        result.setOnOff(studyDto.getOnOff());
        result.setActivityStart(studyDto.getActivityStart());
        result.setActivityDeadline(studyDto.getActivityDeadline());
        result.setRecruitmentDeadline(studyDto.getRecruitmentDeadline());
//        result.setRecruitStatus(studyDto.getRecruitStatus());

        return result;
    }

    @Transactional
    public Study createApplicant(long id, String apply_reason, Authentication authentication) {

        String userId = authentication.getName();
        Member member = memberService.find(userId);

        Study study = findById(id);

        Applicant applicant = Applicant.builder()
                .study(study)
                .member(member)
                .applyReason(apply_reason)
                .participationState(null)    // 처음 참여 상태는 null ( 참여만 한 상태 )
                .build();

        applicantRepository.save(applicant);

        return null;
    }

    /* 해당 회원이 스터디원인지 확인 */
    public boolean checkStudyMember(long studyId, String id) {
        Study study = findById(studyId);
        Member member = memberService.find(id);

        return studyMemberRepository.existsByStudyAndMember(study, member);
    }

    public boolean isApplicant(long id, Authentication authentication) {

        String userId = authentication.getName();

        Study study = findById(id);
        Member member = memberService.find(userId);

        return applicantRepository.existsByMemberAndStudy(member, study);
    }

    public Applicant findByMemberAndStudy(long id, Authentication authentication) {
        String userId = authentication.getName();

        Study study = findById(id);
        Member member = memberService.find(userId);

        if (applicantRepository.existsByMemberAndStudy(member, study))
            return applicantRepository.findByMemberAndStudy(member, study);
        return null;
    }

    // 스터디 참여자 선택 ( 수락 / 거절 )
    @Transactional
    public void selectParticipant(long studyId, String applicantId, boolean isSelect, Authentication authentication) throws Exception {
        if (isRecruiter(studyId, authentication)) {   // 로그인한 사용자가 스터디 개설자라면 참여 상태 변경
            Study study = findById(studyId);
            Member member = memberService.find(applicantId);

            Applicant applicant = applicantRepository.findByMemberAndStudy(member, study);
            applicant.setParticipationState(isSelect);

        } else {
            throw new Exception("스터디 개설자가 아닙니다.");
        }
    }

    // [R] 스터디 참여자 리스트 Select
    public List<Applicant> getParticipants(long id, Authentication authentication) throws Exception {
        System.out.println("스터디 참여자 리스트 서비스 진입 O" + id);
        Study study = findById(id);

        if (isRecruiter(id, authentication)) {
            return applicantRepository.findByStudy(study);
        } else {
            throw new Exception("스터디 개설자가 아닙니다.");
        }
    }

    public boolean isRecruiter(long id, Authentication authentication) {

        String userId = authentication.getName();
        Study study = findById(id);

        if (study.getRecruiter().getId().equals(userId))
            return true;

        return false;
    }

    // 스터디 참여 상태가 true인 사용자 Select
    public List<Applicant> getStudyMember(long id, Authentication authentication) {
        Study study = findById(id);
        return applicantRepository.findByStudyAndParticipationState(study, true);
    }

    @Transactional
    public void createStudyMember(long id, Authentication authentication) {
        List<Applicant> applicants = getStudyMember(id, authentication);

        for (Applicant applicant : applicants) {
            System.out.println(applicant.getMember() + " " + applicant.getStudy());

            StudyMember studyMember = StudyMember.builder()
                    .member(applicant.getMember())
                    .study(applicant.getStudy())
                    .replyAllow(true)
                    .deleteAllow(true)
                    .recruiterAllow(true).build();

            studyMemberRepository.save(studyMember);
        }
    }

    @Transactional
    public void openStudy(long id, Authentication authentication) throws Exception {

        try {
            Study study = findById(id);
            String userId = authentication.getName();
            Member member = memberService.find(userId);
            System.out.println(study.getRecruiter().getId());
            System.out.println(userId);

            if (study.getRecruiter().getId().equals(userId)) {
                createStudyMember(id, authentication);

                // 로그인한 사용자 마지막으로 스터디 참여자로 add
                StudyMember studyMember = StudyMember.builder()
                        .member(member)
                        .study(study)
                        .replyAllow(true)
                        .deleteAllow(false)
                        .recruiterAllow(true).build();

                studyMemberRepository.save(studyMember);

//                LocalDate localDate = LocalDate.now();
////              현재 날짜 기준으로, 스터디 활동 시작일이 현재 날짜보다 같거나 작다면 "진행 중" 상태로
//                if (localDate.isAfter(study.getActivityStart()) || localDate.equals(study.getActivityStart()))
//                    study.setProgressStatus(ProgressStatus.valueOf("IN_PROGRESS"));
//                else
//                    study.setProgressStatus(ProgressStatus.valueOf("BEFORE_PROCEEDING"));

                study.setProgressStatus(ProgressStatus.valueOf("IN_PROGRESS"));
                study.setRecruitStatus(RecruitStatus.valueOf("RECRUITMENT_COMPLETE"));
            }

        } catch (Exception e) {
            throw new Exception("스터디 열기 실패");
        }

    }

    public List<StudyMember> findStudyMember(long id, Authentication authentication) throws Exception {
        Study study = findById(id);
        return studyMemberRepository.findByStudy(study);
    }

    public List<Top5Dto> findStudyRanking() {
        return studyRepository.findTop5();
    }

    public Boolean findStudyDiscontinueAllow(Long studyId, Authentication authentication) {
        Study study = findById(studyId);
        Member member = memberService.find(authentication.getName());

        StudyMember studyMember = studyMemberRepository.findByStudyAndMember(study, member);
        return studyMember.isDeleteAllow();
    }

    @Transactional
    public Boolean studyDiscontinueAllow(Long studyId, Authentication authentication) throws Exception {
        // 한 번 동의하면 취소 불가능?
        Study study = findById(studyId);
        Member member = memberService.find(authentication.getName());

        StudyMember studyMember = studyMemberRepository.findByStudyAndMember(study, member);
        studyMember.setDeleteAllow(true);
        studyMemberRepository.save(studyMember);

        // 모든 스터디원이 동의했는지 확인 -> 스터디 "중단"으로 변경
        List<StudyMember> studyMembers = findStudyMember(studyId, authentication);

        if (studyMembers.stream().allMatch(StudyMember::isDeleteAllow)) {
            // 모두 동의한 경우 스터디 중단
            study.setProgressStatus(ProgressStatus.DISCONTINUE);
            studyRepository.save(study);
        }
        return true;
    }

    // 스터디 모집 마감일이 지나면 "모집 중" -> "모집 완료"로 상태 변경
    @Transactional
    public void checkStudyRecruitmentDeadline() {
        LocalDate localDate = LocalDate.now();
        List<Study> result = studyRepository.findByRecruitmentDeadlineBefore(localDate);
        for (Study s : result)
            s.setRecruitStatus(RecruitStatus.RECRUITMENT_COMPLETE);
    }

    // 스터디 활동 마감일이 지나면 "진행 중" -> "진행 완료"로 상태 변경
    @Transactional
    public void checkStudyActivityDeadline() {
        LocalDate localDate = LocalDate.now();
        ProgressStatus progressStatus = ProgressStatus.IN_PROGRESS;

//        활동 진행 상태가 "진행 중"인 것 중에 마감일이 지난 스터디 리스트 추출
        List<Study> result = studyRepository.findByActivityDeadlineBeforeAndProgressStatus(localDate, progressStatus);
        for (Study s : result)
            s.setProgressStatus(ProgressStatus.WRAP_UP);
    }

//    스터디 활동 시작일이 시작되면 "진행 중"로 상태 변경
//    @Transactional
//    public void checkStudyActivityStart() {
//        LocalDate localDate = LocalDate.now();
//        ProgressStatus progressStatus = ProgressStatus.BEFORE_PROCEEDING;
//        List<Study> result = studyRepository.findByActivityStartGreaterThanEqualAndProgressStatus(localDate, progressStatus);
//        for (Study s : result)
//            s.setProgressStatus(ProgressStatus.IN_PROGRESS);
//    }

}
