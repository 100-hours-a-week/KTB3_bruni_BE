package com.example.my_community.auth.controller;

import com.example.my_community.auth.AuthSessionKeys;
import com.example.my_community.auth.dto.LoginRequest;
import com.example.my_community.common.exception.UnauthorizedException;
import com.example.my_community.user.domain.User;
import com.example.my_community.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Operation(summary = "로그인(세션 생성)")
    @ApiResponse(responseCode = "204", description = "로그인 성공 (세션 생성)")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest req, @Parameter(hidden = true) HttpSession session) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다."));
        // 비밀번호 검증(일단 평문 비교 이후에 리펙토링 필요)
        if (!user.getPassword().equals(req.getPassword())) {
            throw new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 세션에 로그인 유저 ID 저장
        session.setAttribute(AuthSessionKeys.LOGIN_USER_ID, user.getId());

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

    // 회원 정보 수정 매핑 : 수정 필요
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> me(@Parameter(hidden = true) HttpServletRequest request) {
        Long uid = 1L;
        return ResponseEntity.ok(Map.of("userId", uid));
    }
}
