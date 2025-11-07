package com.example.my_community.common.advice;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 오류 응답")
public class ErrorResponse {
    @Schema(description = "오류 발생 시각(ISO-8601)", example = "2025-10-21T12:34:56.123+09:00")
    private final String timestamp = java.time.OffsetDateTime.now().toString();

    @Schema(description = "HTTP 상태코드", example = "401")
    private final int status;

    @Schema(description = "서비스 에러 코드", example = "AUTH-UNAUTHORIZED")
    private final String code;

    @Schema(description = "오류 메시지", example = "로그인이 필요합니다.")
    private final String message;

    @Schema(description = "검증 실패 필드명(없으면 null)", example = "username", nullable = true)
    private final String field; // nullable

    @Schema(description = "요청 경로", example = "/api/auth/me", nullable = true)
    private final String path;  // nullable

    public ErrorResponse(int status, String code, String message, String field, String path) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.field = field;
        this.path = path;
    }

    // getters...
    public String getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getField() { return field; }
    public String getPath() { return path; }
}
