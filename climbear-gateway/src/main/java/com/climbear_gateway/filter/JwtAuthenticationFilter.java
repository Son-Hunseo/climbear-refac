package com.climbear_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret.key}")
    private String secretKey;

    private static final String BEARER_PREFIX = "Bearer ";

    // 인증 통과할 요청들
    private static final List<String> EXCLUDE_PATHS = List.of(
            // for 스프링 서버
            "/api/v1/auth/kakao/login",
            "/api/v1/solutions/non-member/**",
            "/api/v1/problems/non-member/**",
            "/api/v1/centers/list",
            "/api/v1/centers/non-member/**",
            "/api/v1/records/non-member/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui.html",

            // for FastAPI 서버
            "/ai/v1/**",
            "/docs",
            "/openapi.json"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private boolean isExcludedPath(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        return EXCLUDE_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (isExcludedPath(exchange)) {
            return chain.filter(exchange);
        }

        String token = resolveToken(exchange);

        if (token == null) {
            return onError(exchange, "인증 토큰이 없거나 유효하지 않습니다", HttpStatus.UNAUTHORIZED);
        }

        try {
            Claims claims = validateToken(token);

            String tokenType = (String) claims.get("type");
            ServerWebExchange mutatedExchange = exchange;

            if ("ACCESS".equals(tokenType)) {
                String email = claims.getSubject();
                Integer userId = claims.get("userId", Integer.class);

                mutatedExchange = exchange.mutate()
                        .request(r -> r.headers(headers -> {
                            headers.add("X-USER-ID", String.valueOf(userId));
                            headers.add("X-USER-EMAIL", email);
                        }))
                        .build();
            }
            else if ("REFRESH".equals(tokenType)) {
                Integer userId = claims.get("userId", Integer.class);

                mutatedExchange = exchange.mutate()
                        .request(r -> r.headers(headers -> {
                            headers.add("X-USER-ID", String.valueOf(userId));
                        }))
                        .build();
            }

            return chain.filter(mutatedExchange);
        } catch (JwtException e) {
            log.error("JWT 검증 실패: {}", e.getMessage());
            return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
        }
    }

    private String resolveToken(ServerWebExchange exchange) {
        String bearer = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER_PREFIX)) {
            return bearer.substring(7);
        }
        return null;
    }

    private Claims validateToken(String token) {
        byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);

        DataBufferFactory bufferFactory = response.bufferFactory();
        byte[] bytes = err.getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(bufferFactory.wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -1; // 모든 필터보다 우선 실행
    }
}
