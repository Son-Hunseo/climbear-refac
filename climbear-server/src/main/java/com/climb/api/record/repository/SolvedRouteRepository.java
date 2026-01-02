package com.climb.api.record.repository;

import com.climb.api.record.domain.entity.SolvedRoute;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolvedRouteRepository extends MongoRepository<SolvedRoute, String> {

    List<SolvedRoute> findByUserId(Integer userId);

    List<SolvedRoute> findByProblemId(Integer integer);

    List<SolvedRoute> findByProblemIdInAndHeightBetween(List<Integer> problemIds, Double minHeight, Double maxHeight);
}
