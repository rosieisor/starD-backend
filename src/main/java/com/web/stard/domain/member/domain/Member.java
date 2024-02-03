package com.web.stard.domain.member.domain;

import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter @Setter
@Entity
@NoArgsConstructor(force = true)
@Table(name="Member")
@SecondaryTable(name = "Member_Detail",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "Member_Detail_Id"))
public class Member implements UserDetails {

    @Id
    private String id;

    @NotNull
    private String nickname;

    @NotNull
    private String name;

    @NotNull
    private String password;

    @NotNull
    private String email;

    @NotNull
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role roles; // [ADMIN, USER]

    // MemberDetail 컬럼
    @Column(table = "Member_Detail",
            name = "Member_City")
    private String city;

    @Column(table = "Member_Detail",
            name = "Member_District")
    private String district;

    @OneToOne
    @JoinColumn(name = "profile_id")
    private Profile profile; // 프로필

    @NotNull
    @Column(table = "Member_Detail",
            name = "Member_Report_Count")
    private Integer reportCount; // 개인 누적 신고수


    @Builder
    public Member(String id, String name, String email, String password, String phone, String nickname) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.nickname = nickname;
    }

    @Builder
    public Member(String id, String password) {
        this.id = id;
        this.password = password;
    }

    // 계정의 권한 목록 return
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.roles.getRoleValue()));
        return authorities;
    }

    // 계정의 고유한 값 ex) PK return
    @Override
    public String getUsername() {
        return this.id;
    }

    // 계정의 만료 여부 return
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정의 잠김 여부 return
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료 여부 return
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정의 활성화 여부 return
    @Override
    public boolean isEnabled() {
        return true;
    }
}