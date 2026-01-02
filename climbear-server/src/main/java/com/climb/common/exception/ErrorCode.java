package com.climb.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // =========== 인증 관련 ===========
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "error.auth.invalid.token", "유효하지 않은 JWT 토큰입니다"),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "error.auth.expired.token", "만료된 JWT 토큰입니다"),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "error.auth.unsupported.token", "지원되지 않는 JWT 토큰입니다"),
    EMPTY_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "error.auth.empty.token", "JWT 토큰이 비어있습니다"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "error.auth.expired.refresh.token", "리프레시 토큰이 만료되었습니다"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "error.auth.invalid.refresh.token","유효하지 않은 리프레시 토큰입니다"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "error.user.not.found", "사용자를 찾을 수 없습니다"),
    USER_PRINCIPAL_NOT_FOUND(HttpStatus.UNAUTHORIZED, "error.principal.not.found", "인증된 사용자 정보를 찾을 수 없습니다"),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "error.auth.unauthorized.access", "인증되지 않은 접근입니다"),

    // =========== Common ===========
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "error.invalid.input", "유효하지 않은 입력값입니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "error.invalid.type", "올바르지 않은 타입입니다"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "error.resource.not.found", "리소스를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.server", "서버 오류가 발생했습니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "error.method.not.allowed", "지원하지 않는 HTTP 메소드입니다"),
    FUNCTIONALITY_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "error.functionality.not.supported", "지원하지 않는 기능입니다"),
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.external.api", "API 호출 중 오류가 발생했습니다"),

    // =========== Business ===========
    PROBLEM_NOT_FOUND(HttpStatus.NOT_FOUND, "error.problem.not.found", "해당 문제를 찾을 수 없습니다");

    private final HttpStatus status;
    private final String messageKey;
    private final String defaultMessage;

    /**
     * 기본 메시지를 반환합니다 (기존 코드와의 호환성 유지).
     * @return 기본 오류 메시지
     */
    public String getMessage() {
        return defaultMessage;
    }

    /**
     * MessageSource를 통해 국제화된 메시지를 반환합니다.
     * @param messageSource 메시지 소스
     * @param args 메시지 파라미터
     * @return 국제화된 메시지
     */
    public String getMessage(MessageSource messageSource, Object... args) {
        return messageSource.getMessage(
                messageKey,
                args,
                defaultMessage,
                LocaleContextHolder.getLocale()
        );
    }
}