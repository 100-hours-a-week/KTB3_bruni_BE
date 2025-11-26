package com.example.my_community.auth;

import com.example.my_community.auth.security.CustomUserDetails;
import com.example.my_community.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Auth {
    /**
     * 현재 로그인한 유저의 ID를 필수로 가져오기
     * 없으면 UnauthorizedException 발생
     */
    public Long requireUserId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        return userId;
    }

    /**
     * 현재 로그인한 유저의 ID를 가져온다. (없으면 null 반환)
     */
    public Long getCurrentUserId() {
        // 1) SecurityContext 우선
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
                return customUserDetails.getId();
        }

        // anonymous 인 경우
        return null;
    }


}
