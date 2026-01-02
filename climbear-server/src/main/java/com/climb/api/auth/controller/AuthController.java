package com.climb.api.auth.controller;

import com.climb.api.auth.controller.swagger.AuthControllerSwagger;
import com.climb.api.auth.domain.dto.request.KakaoAuthRequestDTO;
import com.climb.api.auth.domain.dto.request.TokenRefreshRequestDTO;
import com.climb.api.auth.domain.dto.response.KakaoAuthResponseDTO;
import com.climb.api.auth.domain.dto.response.TokenResponseDTO;
import com.climb.api.auth.security.JwtTokenProvider;
import com.climb.api.auth.service.AuthService;
import com.climb.api.auth.service.RefreshTokenService;
import com.climb.api.user.service.UserService;
import com.climb.common.response.CustomApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증/인가 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final AuthService authService;

    @AuthControllerSwagger.RefreshTokenApi
    @PostMapping("/refresh")
    public ResponseEntity<CustomApiResponse<TokenResponseDTO>> refreshToken(@RequestHeader("X-USER-ID") Integer userId,
                                                                            @RequestBody TokenRefreshRequestDTO request) {
        // 리프레시 토큰 검증
        refreshTokenService.validateRefreshToken(request.refreshToken());

        String email = userService.getEmailByUserId(userId);

        // 새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(email, userId);
        String newRefreshToken = refreshTokenService.createRefreshToken(userId);

        return ResponseEntity.ok(CustomApiResponse.success(new TokenResponseDTO(newAccessToken, newRefreshToken)));
    }

    @AuthControllerSwagger.KakaoLoginApi
    @PostMapping("/kakao/login")
    public ResponseEntity<CustomApiResponse<KakaoAuthResponseDTO>> kakaoLogin(@RequestBody KakaoAuthRequestDTO request) {
        KakaoAuthResponseDTO response = authService.authenticateKakaoUser(request.accessToken());
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @AuthControllerSwagger.LogoutApi
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("X-USER-ID") Integer userId) {
        refreshTokenService.deleteRefreshToken(userId);
        return ResponseEntity.noContent().build();
    }
}
