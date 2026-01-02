package com.climb.api.problem.repository;

import com.climb.api.problem.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByCenterCenterIdAndHoldColor(Integer centerId, String holdColor);
}
