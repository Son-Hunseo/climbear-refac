package com.climb.api.user.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserInfoResponseDTO(
        @Schema(description = "사용자 이메일", example = "test@test.com")
        String email,

        @Schema(description = "카카오 닉네임", example = "최강삼성")
        String nickname,

        @Schema(description = "키", example = "160.0")
        Double height,

        @Schema(description = "팔 길이", example = "150.0")
        Double armSpan,

        @Schema(description = "경험치", example = "43875")
        Long exp
) {}
