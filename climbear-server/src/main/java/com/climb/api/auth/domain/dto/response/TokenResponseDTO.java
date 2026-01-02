package com.climb.api.auth.domain.dto.response;

public record TokenResponseDTO(
        String accessToken,
        String refreshToken
) {}
