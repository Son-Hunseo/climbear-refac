package com.climb.api.problem.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "등록된 문제ID, 카테고리ID 받는 DTO")
@Builder
public record SaveProblemResponseDTO(
        Integer problemId,
        Integer categoryId
) {}
