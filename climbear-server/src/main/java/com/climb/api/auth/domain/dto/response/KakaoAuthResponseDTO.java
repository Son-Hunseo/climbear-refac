package com.climb.api.auth.domain.dto.response;

import lombok.Builder;

@Builder
public record KakaoAuthResponseDTO(
        String accessToken,
        String refreshToken,
        Integer userId,
        String email,
        String nickname,
        boolean isNewUser
) {}
