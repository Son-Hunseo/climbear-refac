package com.climb.api.record.domain.dto.request;

import com.climb.api.problem.domain.type.Route;

public record SaveSuccessRouteRequestDTO(
        Integer problemId,
        Route route,
        Integer time,
        Double height
) {}
