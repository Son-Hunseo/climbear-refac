package com.climb.api.center.domain.type;

import lombok.Builder;

@Builder
public record LevelCount(
        String level,
        int count
) {}
