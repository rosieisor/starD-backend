package com.web.stard.service;

import com.web.stard.domain.Interest;
import com.web.stard.domain.Member;
import com.web.stard.domain.Role;
import com.web.stard.repository.InterestRepository;
import com.web.stard.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Member find(String id) {
        Optional<Member> result = memberRepository.findById(id);

        if(result.isPresent())
            return result.get();
        return null;
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
    public boolean deleteMember(String id, String password) {
        Member member = find(id);

        if (!checkPw(id, password)) { // 비밀번호 틀림
            return false; // 실패
        }

        // 추후 작성 (멤버와 엮이는 게 많아지면 조건 등등 추가 필요)
        memberRepository.delete(member);


        if (find(id) == null) { // 삭제됨
            return true;
        }
        return false;
    }

    public Member findId(String email, String phone) {
        return memberRepository.findByEmailAndPhone(email, phone);
    }


    // authentication으로 닉네임 찾기
    public Member findNickNameByAuthentication(Authentication authentication) {
        return memberRepository.findNicknameById(authentication.getName());
    }
}
