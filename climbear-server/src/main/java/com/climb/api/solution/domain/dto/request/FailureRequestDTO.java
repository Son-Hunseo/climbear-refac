package com.climb.api.solution.domain.dto.request;

public record FailureRequestDTO(
        Integer leftHand,
        Integer rightHand,
        Integer leftFoot,
        Integer rightFoot
) {}
