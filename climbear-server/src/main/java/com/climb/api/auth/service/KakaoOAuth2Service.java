package com.climb.api.auth.service;

import com.climb.api.auth.domain.entity.KakaoUserInfo;
import com.climb.api.user.domain.entity.User;
import com.climb.api.user.repository.UserRepository;
import com.climb.common.exception.BusinessException;
import com.climb.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuth2Service {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private static final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    @Transactional
    public KakaoUserInfo getUserInfo(String accessToken) {
        try {
            // 카카오 API 호출을 위한 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URI,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }
            );

            Map<String, Object> attributes = response.getBody();

            return KakaoUserInfo.fromAttributes(attributes);
        } catch (RestClientException e) {
            log.error("카카오 API 호출 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "카카오 API 호출 중 오류가 발생했습니다");
        }
    }

    @Transactional
    public User processUserLogin(KakaoUserInfo kakaoUserInfo) {
        // 카카오 ID를 기반으로 사용자 조회 또는 생성
        Long kakaoId = kakaoUserInfo.getId();

        return userRepository.findByKakaoId(kakaoId)
                // Optional이 값을 가지고 있으면 map 내부 코드 실행
                .map(existingUser -> {
                    if (!existingUser.getNickname().equals(kakaoUserInfo.getNickname())) {
                        existingUser.updateNickname(kakaoUserInfo.getNickname());
                        return userRepository.save(existingUser);
                    }
                    return existingUser;
                })
                // Optional이 비어있으면 orElseGet 내부 코드 실행
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(kakaoUserInfo.getEmail())
                            .nickname(kakaoUserInfo.getNickname())
                            .kakaoId(kakaoId)
                            .build();
                    return userRepository.save(newUser);
                });
    }
}
