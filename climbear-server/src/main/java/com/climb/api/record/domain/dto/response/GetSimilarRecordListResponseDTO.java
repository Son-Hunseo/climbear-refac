package com.climb.api.record.domain.dto.response;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record GetSimilarRecordListResponseDTO(
        Integer problemId,
        Integer userId,
        Double height,
        Integer successRound,
        Integer time,
        Timestamp solvedDate
) {}