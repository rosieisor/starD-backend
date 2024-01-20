package com.web.stard.repository;

import com.web.stard.domain.Member;
import com.web.stard.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Member findPasswordById(String id);

    Optional<Member> findById(String id);

    boolean existsByNickname(String nickname);

    boolean existsById(String id);

    Member findNicknameById(String id);

    Member findByNickname(String nickname);

    List<Member> findByEmailAndPhone(String email, String phone);
}
