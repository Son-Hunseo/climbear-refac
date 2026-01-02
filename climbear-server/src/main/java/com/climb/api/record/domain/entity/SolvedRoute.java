package com.climb.api.record.domain.entity;

import com.climb.api.problem.domain.type.Route;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "solved_routes")
public class SolvedRoute {

    @Id
    private String id;

    @Field("problem_id")
    private Integer problemId;

    @Field("user_id")
    private Integer userId;

    @Field("success_round")
    private Integer successRound;

    @Field("route")
    private Route route;

    @Field("time")
    private Integer time;

    @Field("height")
    private Double height;

    @Field("level")
    private String level;

    @Field("center_name")
    private String centerName;

    @Field("created_at")
    private Timestamp createdAt;
}
