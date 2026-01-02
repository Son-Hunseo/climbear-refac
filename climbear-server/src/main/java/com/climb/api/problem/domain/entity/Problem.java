package com.climb.api.problem.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "problems")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Problem {

    @Id
    @Column(name = "problem_id")
    private Integer problemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "try_count", nullable = false)
    @Builder.Default
    private Integer tryCount = 0;

    @Column(name = "success_count", nullable = false)
    @Builder.Default
    private Integer successCount = 0;

    public void incrementTryCount() {
        this.tryCount++;
    }

    public void incrementSuccessCount() {
        this.successCount++;
    }
}