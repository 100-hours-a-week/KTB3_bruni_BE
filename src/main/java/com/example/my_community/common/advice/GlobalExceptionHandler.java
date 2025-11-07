package com.example.my_community.common.advice;

import com.example.my_community.common.exception.ForbiddenException;
import com.example.my_community.common.exception.NotFoundException;
import com.example.my_community.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice  // 전역 컨트롤러 예외 처리기
public class GlobalExceptionHandler {

    // 400: 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        FieldError fe = ex.getBindingResult().getFieldError();
        String field = fe != null ? fe.getField() : null;
        String msg = fe != null ? fe.getDefaultMessage() : "검증 오류";
        return ResponseEntity.badRequest().body(
                new ErrorResponse(400, "COMMON-VALIDATION", msg, field, req.getRequestURI())
        );
    }

    // 401: 미인증
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class)
            )
    )
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex,
                                                            HttpServletRequest req) {
        return ResponseEntity.status(401).body(
                new ErrorResponse(401, "AUTH-UNAUTHORIZED", ex.getMessage(), null, req.getRequestURI())
        );
    }

    // 403: 권한 없음 (커스텀 예외)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex, HttpServletRequest req) {
        return ResponseEntity.status(403).body(
                new ErrorResponse(403, "AUTH-FORBIDDEN", ex.getMessage(), null, req.getRequestURI())
        );
    }

    // 404: 리소스 없음
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(404).body(
                new ErrorResponse(404, "POST-NOT_FOUND", "리소스를 찾을 수 없습니다.", null, req.getRequestURI())
        );
    }


    private boolean isSwaggerOrOpenApi(HttpServletRequest req) {
        String u = req.getRequestURI();
        return u.startsWith("/v3/api-docs")
                || u.startsWith("/swagger-ui")
                || u.startsWith("/api-docs"); // 커스텀 path를 쓸 수도 있으니 포함
    }

    // 500: 기타
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleEtc(Exception ex, HttpServletRequest req) throws Exception {
        if (isSwaggerOrOpenApi(req)) {
            // 문서 생성 중 발생하는 예외는 래핑하지 말고 그대로 던져
            throw ex;
        }
        return ResponseEntity.status(500).body(
                new ErrorResponse(500, "COMMON-UNKNOWN", "서버 오류가 발생했습니다.", null, req.getRequestURI())
        );
    }
}
