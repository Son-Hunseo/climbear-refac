package com.climb.api.problem.domain.entity;

import com.climb.api.problem.domain.type.Hold;
import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "problem_datas")
public class ProblemData {

    @Id
    @Field("problem_id")
    private Integer problemId;

    @Field("category_id")
    private Integer categoryId;

    @Field("image_name")
    private String imageName;

    @Field("selected")
    private List<Hold> selected;

    @Field("start_hold")
    private List<Integer> startHold;

    @Field("end_hold")
    private List<Integer> endHold;

    @Field("choice_color")
    private String choiceColor;

    @Field("level")
    private String level;

    @Field("pixel_grid")
    private Integer pixelGrid;
}
