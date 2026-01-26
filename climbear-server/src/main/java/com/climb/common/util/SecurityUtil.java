package com.climb.common.util;

import com.climb.api.user.domain.entity.User;
import com.climb.common.exception.BusinessException;
import com.climb.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {}

    // SecurityContext에서 현재 인증된 사용자 ID 추출
    public static Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user.getUserId();
        }

        throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    // SecurityContext에서 현재 인증된 사용자 이메일 추출
    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) {
            return user.getEmail();
        }
        throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    // 사용자가 인증되었는지 확인
    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User;
    }
}
