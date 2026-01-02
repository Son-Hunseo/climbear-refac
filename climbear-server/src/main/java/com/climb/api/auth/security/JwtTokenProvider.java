package com.climb.api.auth.security;

import com.climb.api.auth.config.JwtConfig;
import com.climb.common.exception.BusinessException;
import com.climb.common.exception.ErrorCode;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    // 사용자 인증 정보 기반 액세스 토큰 생성
    public String createAccessToken(String email, Integer userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtConfig.getAccessTokenValidity());

        log.debug("Access 토큰 생성: 사용자={}, 만료={}", email, validity);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("type", "ACCESS")
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(jwtConfig.key(), SignatureAlgorithm.HS512)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(Integer userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtConfig.getRefreshTokenValidity());

        log.debug("Refresh 토큰 생성: 사용자={}, 만료={}", userId, validity);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim("userId", userId)
                .claim("type", "REFRESH")
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(jwtConfig.key(), SignatureAlgorithm.HS512)
                .compact();
    }

    // 리프레시 토큰의 만료일자 추출
    public Date getRefreshTokenExpiry(String refreshToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtConfig.key())
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getExpiration();
        } catch (Exception e) {
            log.error("리프레시 토큰 만료일자 추출 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
