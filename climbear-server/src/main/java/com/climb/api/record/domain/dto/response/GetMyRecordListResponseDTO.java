package com.climb.api.record.domain.dto.response;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record GetMyRecordListResponseDTO(
        Integer problemId,
        String level,
        Integer successCount,
        Integer tryCount,
        Integer minTime,
        Timestamp lastSolvesDate,
        String centerName
)
{}
