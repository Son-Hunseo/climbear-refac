package com.climb.api.problem.repository;

import com.climb.api.problem.domain.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Integer> {

    Optional<Problem> findByProblemIdAndUserId(Integer problemId, Integer userId);

    List<Problem> findByUserIdAndProblemIdIn(Integer userId, Set<Integer> attemptedProblemIds);

    @Query("SELECT p.problemId FROM Problem p WHERE p.category.categoryId = :categoryId")
    List<Integer> findProblemIdsByCategory_CategoryId(@Param("categoryId") Integer categoryId);
}
