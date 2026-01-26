package com.climb.api.auth.service;

import com.climb.api.auth.domain.entity.RefreshToken;
import com.climb.api.auth.repository.RefreshTokenRepository;
import com.climb.api.auth.security.JwtTokenProvider;
import com.climb.common.exception.BusinessException;
import com.climb.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 리프레시 토큰 생성 및 저장/갱신
    @Transactional
    public String createRefreshToken(Integer userId) {
        String token = jwtTokenProvider.createRefreshToken(userId);
        Date expiry = jwtTokenProvider.getRefreshTokenExpiry(token);
        Timestamp expiryDate = new Timestamp(expiry.getTime());

        try {
            // 기존 토큰이 있는지 확인하고 있으면 갱신, 없으면 새로 생성
            return refreshTokenRepository.findByUserId(userId)
                    .map(refreshToken -> {
                        refreshToken.updateToken(token, expiryDate);
                        refreshTokenRepository.save(refreshToken); // 낙관적 락 작동
                        return token;
                    })
                    .orElseGet(() -> {
                        RefreshToken newToken = RefreshToken.builder()
                                .userId(userId)
                                .token(token)
                                .expiryDate(expiryDate)
                                .build();
                        refreshTokenRepository.save(newToken);
                        return token;
                    });
        } catch (OptimisticLockingFailureException e) {
            log.debug("동시 토큰 갱신 시도 발생: userId={}", userId);
            return handleOptimisticLockFailure(userId); // 충돌 발생 시 최신 토큰 반환
        }
    }

    // 낙관적 락 충돌 처리
    private String handleOptimisticLockFailure(Integer userId) {
        // 재시도 대신 최신 토큰 반환 방식 - 동시 요청 중 하나가 이미 성공했으므로
        return refreshTokenRepository.findByUserId(userId)
                .map(RefreshToken::getToken)
                .orElseGet(() -> {
                    // 극히 드문 경우지만, 토큰이 삭제된 경우 새로 생성
                    return createRefreshTokenWithRetry(userId);
                });
    }

    // 실패 시 재시도 로직
    private String createRefreshTokenWithRetry(Integer userId) {
        String token = jwtTokenProvider.createRefreshToken(userId);
        Date expiry = jwtTokenProvider.getRefreshTokenExpiry(token);
        Timestamp expiryDate = new Timestamp(expiry.getTime());

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiryDate(expiryDate)
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    // 리프레시 토큰 검증 및 userId 반환
    @Transactional(readOnly = true)
    public Integer validateRefreshTokenAndGetUserId(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        // 토큰 만료 여부 확인
        if (refreshToken.isExpired()) {
            throw new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        return refreshToken.getUserId();
    }

    @Transactional
    public void deleteRefreshToken(Integer userId) {
        refreshTokenRepository.findByUserId(userId)
                .ifPresent(refreshToken -> {
                    refreshTokenRepository.delete(refreshToken);
                    log.info("사용자 {}의 리프레시 토큰이 성공적으로 삭제되었습니다.", userId);
                });
    }
}
