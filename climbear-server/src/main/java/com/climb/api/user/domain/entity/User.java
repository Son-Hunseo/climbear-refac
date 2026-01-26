package com.climb.api.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;

@Table(name = "users")
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "kakao_id", nullable = false)
    private Long kakaoId;

    @Column(length = 100)
    private String email;

    @Column(nullable = false, length = 30)
    private String nickname;

    private Double height;

    @Column(name = "arm_span")
    private Double armSpan;

    @Column(name = "exp")
    @Builder.Default
    private Long exp = 0L;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "resign_flag", nullable = false)
    @Builder.Default
    private boolean resignFlag = false;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateHeight(Double height) {
        this.height = height;
    }

    public void updateArmSpan(Double armSpan) {
        this.armSpan = armSpan;
    }

    public void incrementExp(long exp){
        this.exp += exp;
    }

    public void resignUser() {
        this.resignFlag = true;
        this.email = "탈퇴회원_" + userId;
        this.nickname = "탈퇴회원_" + userId;
        this.kakaoId = -1L;
    }

    // UserDetails 구현
    @Override
    public String getUsername() {
        return this.userId.toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !resignFlag;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !resignFlag;
    }
}
