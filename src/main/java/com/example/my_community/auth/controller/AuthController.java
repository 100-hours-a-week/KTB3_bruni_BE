package com.example.my_community.auth.controller;

import com.example.my_community.auth.Auth;
import com.example.my_community.auth.AuthSessionKeys;
import com.example.my_community.auth.SessionUser;
import com.example.my_community.common.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth", description = "세션 기반 인증 API")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final Auth auth; // 인증 인터페이스

    public AuthController(Auth auth) {
        this.auth = auth;
    }

    // userId를 세션에 저장하여 인증하는 방식
    @Operation(summary = "로그인(세션 생성)")
    @ApiResponse(responseCode = "204", description = "로그인 성공 (세션 생성)")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam Long userId, @Parameter(hidden = true) HttpSession session) {
        session.setAttribute(AuthSessionKeys.LOGIN_USER_ID, userId);
        return ResponseEntity.noContent().build(); // 204
    }

    @Operation(summary = "로그아웃(세션 만료)")
    @ApiResponse(responseCode = "204", description = "로그아웃 성공 (세션 무효화)")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Parameter(hidden = true) HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build(); // 204
    }

    static class MeResponse {
        @Schema(example = "1")
        public Long userId;
        public MeResponse(Long userId) { this.userId = userId; }
    }

    @Operation(summary = "내 세션 확인")
    @ApiResponse(
            responseCode = "200",
            description = "세션에 저장된 사용자",
            content = @Content(schema = @Schema(implementation = MeResponse.class))
    )
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> me(@Parameter(hidden = true)HttpServletRequest request) {
        Long uid = auth.requireUserId(request);
        return ResponseEntity.ok(Map.of("userId", uid));
    }
}
