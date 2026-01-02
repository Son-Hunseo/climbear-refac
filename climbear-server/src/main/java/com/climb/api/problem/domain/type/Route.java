package com.climb.api.problem.domain.type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    List<Integer> leftHand;
    List<Integer> rightHand;
    List<Integer> leftFoot;
    List<Integer> rightFoot;
}
