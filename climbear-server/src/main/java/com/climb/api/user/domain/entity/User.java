package com.climb.api.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Table(name = "users")
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "kakao_id", nullable = false)
    private Long kakaoId;

    @Column(nullable = false, length = 100)
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
}
