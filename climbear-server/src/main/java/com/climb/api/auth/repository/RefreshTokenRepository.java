package com.climb.api.auth.repository;

import com.climb.api.auth.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByUserId(Integer userId);

    Optional<RefreshToken> findByToken(String token);
}
