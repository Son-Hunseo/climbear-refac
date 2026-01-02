package com.climb.api.user.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClimbingLevel {
    WHITE(1, "white", 50, 500),
    YELLOW(2, "yellow", 100, 3000),
    ORANGE(3, "orange", 200, 8000),
    GREEN(4, "green", 350, 16000),
    BLUE(5, "blue", 700, 30000),
    RED(6, "red", 1200, 60000),
    PURPLE(7, "purple", 1800, 90000),
    GRAY(8, "gray", 2500, 150000),
    PINK(9, "pink", 3500, 200000);

    private final int level;
    private final String levelName;
    private final long gainExp;
    private final long maxExp;
}
