package com.climb.api.user.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserExpResponseDTO(
        @Schema(description = "현재 사용자의 경험치", example = "5000")
        Long exp,

        @Schema(description = "현재 레벨에서의 최대 경험치", example = "8000")
        Long maxExp,

        @Schema(description = "현재 레벨 이름", example = "orange")
        String levelName,

        @Schema(description = "다음 레벨 이름 (최고 레벨인 경우 현재 레벨과 동일)", example = "green")
        String nextLevelName
) {}
