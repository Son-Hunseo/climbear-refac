package com.climb.api.solution.domain.dto.request;

public record NonMemberFailureRequestDTO(
        Integer problemId,
        Double height,
        Double armSpan,
        Integer leftHand,
        Integer rightHand,
        Integer leftFoot,
        Integer rightFoot
){}
