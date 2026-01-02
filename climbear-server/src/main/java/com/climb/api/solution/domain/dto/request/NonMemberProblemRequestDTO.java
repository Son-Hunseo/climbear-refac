package com.climb.api.solution.domain.dto.request;

public record NonMemberProblemRequestDTO(
        Integer problemId,
        Double height,
        Double armSpan
) {}
