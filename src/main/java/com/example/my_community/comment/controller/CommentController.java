package com.example.my_community.comment.controller;

import com.example.my_community.auth.Auth;
import com.example.my_community.auth.SessionUser;
import com.example.my_community.comment.dto.CommentCreateReq;
import com.example.my_community.comment.dto.CommentRes;
import com.example.my_community.comment.service.CommentService;
import com.example.my_community.common.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comments", description = "댓글 API")
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {
    private final CommentService service;
    private final Auth auth;

    public CommentController(CommentService service, Auth auth) {
        this.service = service;
        this.auth = auth;
    }

    /** 생성: POST /api/posts/{postId}/comments */
    @Operation(summary = "댓글 생성")
    @ApiResponse(responseCode = "200", description = "생성 성공(본문에 생성 결과 반환)")
    @PostMapping(value = "/api/posts/{postId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentRes> create(@PathVariable Long postId,
                                             @Valid @RequestBody CommentCreateReq req, HttpServletRequest request) {
        // 로그인 여부 확인
        Long uid = auth.requireUserId(request);
        return ResponseEntity.ok(service.create(postId, req, uid));
    }

    /** 페이지 래퍼 */
    @Schema(name = "CommentPageResponse", description = "댓글 페이지 응답")
    public static class PageResponse<T> {
        public final List<T> content;
        public final int page, size;
        public final long totalElements, totalPages;
        public PageResponse(List<T> c, int p, int s, long te, long tp) {
            this.content = c; this.page = p; this.size = s; this.totalElements = te; this.totalPages = tp;
        }
    }

    /** 목록: GET /api/posts/{postId}/comments */
    @Operation(summary = "댓글 목록 조회(페이지)")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<PageResponse<CommentRes>> list(@PathVariable Long postId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(defaultValue = "createdAt") String sort,
                                                         @RequestParam(defaultValue = "desc") String dir) {
        List<CommentRes> content = service.listByPost(postId, page, size, sort, dir);
        long total = service.countByPost(postId);
        long totalPages = (long) Math.ceil((double) total / size);
        return ResponseEntity.ok(new PageResponse<>(content, page, size, total, totalPages));
    }

    /** 삭제: DELETE /api/comments/{id} */
    @Operation(summary = "댓글 삭제(작성자만)")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long uid = auth.requireUserId(request);
        service.delete(id, uid);
        return ResponseEntity.noContent().build();
    }


}
