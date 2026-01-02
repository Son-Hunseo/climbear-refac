package com.climb.common.util;

import com.climb.common.exception.BusinessException;
import com.climb.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SecurityUtil {

    private SecurityUtil() {}

    // 현재 요청의 X-USER-ID 헤더에서 사용자 ID 추출
    public static Integer getCurrentUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = request.getHeader("X-USER-ID");

        if (userId == null || userId.isEmpty()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return Integer.valueOf(userId);
    }

    // 현재 요청의 X-USER-EMAIL 헤더에서 사용자 이메일 추출
    public static String getCurrentUserEmail() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String email = request.getHeader("X-USER-EMAIL");

        if (email == null || email.isEmpty()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return email;
    }

    // 사용자가 인증되었는지 확인 (X-USER-ID 헤더 존재 여부)
    public static boolean isAuthenticated() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = request.getHeader("X-USER-ID");
        return userId != null && !userId.isEmpty();
    }
}
