package com.climb.api.solution.domain.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ProblemSolutionResponseDTO (
        List<Integer> leftHand,
        List<Integer> rightHand,
        List<Integer> leftFoot,
        List<Integer> rightFoot
) {}
