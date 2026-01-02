package com.climb.api.auth.domain.dto.request;

public record TokenRefreshRequestDTO(
        String refreshToken
) {}
