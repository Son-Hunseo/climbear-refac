package com.climb.api.auth.service;

import com.climb.api.auth.domain.dto.response.KakaoAuthResponseDTO;
import com.climb.api.auth.domain.entity.KakaoUserInfo;
import com.climb.api.auth.security.JwtTokenProvider;
import com.climb.api.user.domain.entity.User;
import com.climb.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuth2Service kakaoOAuth2Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Transactional
    public KakaoAuthResponseDTO authenticateKakaoUser(String kakaoAccessToken) {
        // 카카오 사용자 정보 조회
        KakaoUserInfo kakaoUserInfo = kakaoOAuth2Service.getUserInfo(kakaoAccessToken);

        // 사용자 정보 처리 (기존 사용자 확인 또는 새 사용자 생성)
        boolean isNewUser = !userRepository.existsByKakaoId(kakaoUserInfo.getId());
        User user = kakaoOAuth2Service.processUserLogin(kakaoUserInfo);

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getUserId());
        String refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

        return KakaoAuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .isNewUser(isNewUser)
                .build();
    }
}
