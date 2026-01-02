package com.climb.api.problem.domain.entity;

import com.climb.api.center.domain.entity.Center;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Table(
    name = "categories",
    indexes = {
        @Index(name = "idx_categories_center_id", columnList = "center_id"),
        @Index(name = "idx_categories_hold_color", columnList = "hold_color")
    }
)
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    @Column(name = "hold_color", nullable = false, length = 20)
    private String holdColor;

    @Column(nullable = false, length = 20)
    private String level;

    @Column(name = "hold_count", nullable = false)
    private Integer holdCount;

    @Column(name = "height_diff", nullable = false)
    private Double heightDiff;

    @Column(name = "width_diff", nullable = false)
    private Double widthDiff;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
}