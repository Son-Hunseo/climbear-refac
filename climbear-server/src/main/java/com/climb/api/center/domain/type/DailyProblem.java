package com.climb.api.center.domain.type;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record DailyProblem(
        LocalDate date,
        List<LevelCount> levelCount
) {}
