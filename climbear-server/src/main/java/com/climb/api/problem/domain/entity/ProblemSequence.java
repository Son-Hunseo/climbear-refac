package com.climb.api.problem.domain.entity;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "problem_sequences")
public class ProblemSequence {

    @Id
    private String id;

    private int seq;
}