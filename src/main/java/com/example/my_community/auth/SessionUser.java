package com.example.my_community.auth;

import com.example.my_community.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public class SessionUser implements Auth {

    @Override
    public Optional<Long> currentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 현재 세션 정보(인자값 : false -> 현재 세션 없으면 null 반환)
        if (session == null) return Optional.empty(); // 현재 세션 없으면 'EMPTY'(null을 감싼 객체) 반환
        Object v = session.getAttribute(AuthSessionKeys.LOGIN_USER_ID); // 키(LOGIN_USER_ID)를 통해 값(Long id) 반환
        return (v instanceof Long) ? Optional.of((Long) v) : Optional.empty();
    }

    /**
     * flow
     * 1. Http 요청을 인자(request)로 받음
     * 2. currentUserId(request) 메서드를 통해 Optional(Long 또는 EMPTY)
     * @param request : 클라이언트 요청 정보 담고있는 객체
     */
    @Override
    public Long requireUserId(HttpServletRequest request) {
        return currentUserId(request) // 현재 사용자 id 반환
                .orElseThrow(() -> new UnauthorizedException("로그인이 필요합니다.")); // 반환된 Optional 값이 EMPTY
    }

}
