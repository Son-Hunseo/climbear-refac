package com.climb.api.problem.domain.dto.request;

import com.climb.api.problem.domain.type.Hold;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "등록할 문제 정보 DTO")
@Builder
public record SaveProblemRequestDTO(
        // 문제 분류 정보
        Double latitude,
        Double longitude,
        Double heightDiff,
        Double widthDiff,

        // 저장할 문제 정보
        String imageName,
        List<Hold> selected,
        List<Integer> startHold,
        List<Integer> endHold,
        String choiceColor,
        String level,
        Integer pixelGrid
){}
