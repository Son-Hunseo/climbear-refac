package com.climb.api.problem.domain.type;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hold {

    @Field("hold_id")
    private int holdId;
    private List<Coordinate> coordinates;
}
