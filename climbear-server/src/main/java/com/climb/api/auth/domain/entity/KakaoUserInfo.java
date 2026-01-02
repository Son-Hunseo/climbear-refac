package com.climb.api.auth.domain.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class KakaoUserInfo {
    private Long id;
    private String email;
    private String nickname;

    public static KakaoUserInfo fromAttributes(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) {
            throw new IllegalArgumentException("카카오 계정 정보가 없습니다");
        }

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) {
            throw new IllegalArgumentException("카카오 프로필 정보가 없습니다");
        }

        return KakaoUserInfo.builder()
                .id((Long) attributes.get("id"))
                .email((String) kakaoAccount.get("email"))
                .nickname((String) profile.get("nickname"))
                .build();
    }
}
