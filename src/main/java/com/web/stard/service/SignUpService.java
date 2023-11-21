package com.web.stard.service;

import com.web.stard.domain.Interest;
import com.web.stard.domain.Member;
import com.web.stard.domain.Profile;
import com.web.stard.domain.Role;
import com.web.stard.repository.InterestRepository;
import com.web.stard.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter
@Transactional
@AllArgsConstructor
public class SignUpService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ProfileService profileService;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;

    // 중복 아이디 검증
    public boolean checkDuplicateMember(String id) {
        boolean isDuplicate = memberRepository.existsById(id);
        System.out.println("중복아이디검증: " + isDuplicate);
        return isDuplicate;
    }

    // 중복 닉네임 검증
    public boolean checkNickname(String nickname) {
        return memberRepository.existsByNickname(nickname); // true -> 있음 (사용불가)
    }

    // 비밀번호 정규식 검증
    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$";
        return password.matches(regex);
    }


    // 이메일 정규식 검증
    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        return email.matches(regex);
    }
    
    // 회원 가입
    public Member saveMember(Member member) {
        System.out.println("회원가입 진입");
        // 백엔드에서 추가로 검증
        if (checkDuplicateMember(member.getId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (checkNickname(member.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        if (!isValidPassword(member.getPassword())) {
            throw new IllegalArgumentException("유효하지 않은 비밀번호입니다.");
        }

        if (!isValidEmail(member.getEmail())) {
            throw new IllegalArgumentException("유효하지 않은 이메일 주소입니다.");
        }

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);

        // 회원가입 시 기본적으로 'USER' 권한을 부여
        member.setRoles(Role.USER);

        // 회원 가입 시, 해당 사용자의 Profile 자동 생성
        Profile profile = profileService.createProfile();
        member.setProfile(profile);

        // 신고 횟수 0으로 초기화
        member.setReportCount(0);
        
        return memberRepository.save(member);
    }

    // 거주지, 관심분야 저장
    public void saveAddressAndInterest(String id, String city, String district, String[] interests) {
        Member member = memberService.find(id);

        // 거주지
        member.setCity(city);
        member.setDistrict(district);
        memberRepository.save(member);

        // 관심분야
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

}
