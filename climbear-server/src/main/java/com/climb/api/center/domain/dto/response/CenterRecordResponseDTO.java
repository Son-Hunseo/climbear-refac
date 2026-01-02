package com.climb.api.center.domain.dto.response;

import com.climb.api.center.domain.type.DailyProblem;
import lombok.Builder;

import java.util.List;

@Builder
public record CenterRecordResponseDTO(
        String centerName,
        List<DailyProblem> dailyProblem
) {}
