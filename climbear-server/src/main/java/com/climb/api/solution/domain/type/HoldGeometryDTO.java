package com.climb.api.solution.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
import java.util.Map;
import com.climb.api.solution.service.SolutionService.AABB;
import com.climb.api.solution.service.SolutionService.HoldPair;
import com.climb.api.solution.service.SolutionService.Point;

/**
* 요청별로 생성되는 홀드 기하 정보 묶음
*/
@Getter
@AllArgsConstructor
public class HoldGeometryDTO {
    private final Map<Integer, List<Point>> hullMap;
    private final Map<Integer, AABB>       aabbMap;
    private final Map<HoldPair, Double>    cachedDist;
}