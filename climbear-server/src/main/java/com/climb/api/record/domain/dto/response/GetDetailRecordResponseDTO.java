package com.climb.api.record.domain.dto.response;

import com.climb.api.problem.domain.type.Hold;
import com.climb.api.problem.domain.type.Route;
import lombok.Builder;

import java.sql.Timestamp;
import java.util.List;

@Builder
public record GetDetailRecordResponseDTO(
        Integer problemId,
        Integer successRound,
        Route route,
        Integer time,
        Double height,
        Timestamp createdAt,
        String imageName,
        List<Hold> selected
) {}
