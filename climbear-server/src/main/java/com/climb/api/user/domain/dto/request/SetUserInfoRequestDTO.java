package com.climb.api.user.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SetUserInfoRequestDTO(
        @Schema(description = "키", example = "160.0")
        Double height,

        @Schema(description = "팔 길이", example = "150.0")
        Double armSpan
) {}
