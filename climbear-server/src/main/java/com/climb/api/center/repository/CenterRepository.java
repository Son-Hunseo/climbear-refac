package com.climb.api.center.repository;

import com.climb.api.center.domain.entity.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CenterRepository extends JpaRepository<Center, Integer>{

    @Query(value = "SELECT c FROM Center c WHERE function('ST_Distance_Sphere', function('POINT', c.longitude, c.latitude), function('POINT', :longitude, :latitude)) <= :distance")
    List<Center> findCentersWithinDistance(@Param("latitude") Double latitude, @Param("longitude") Double longitude, @Param("distance") Double distanceInMeters);

    List<Center> findByCenterIdNot(int centerId);
}
