package com.example.my_community.comment.controller;

import com.example.my_community.auth.Auth;
import com.example.my_community.comment.dto.CommentCreateReq;
import com.example.my_community.comment.dto.CommentRes;
import com.example.my_community.comment.dto.CommentUpdateReq;
import com.example.my_community.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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



    @Operation(summary = "댓글 목록 조회")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<List<CommentRes>> list(@PathVariable Long postId,
                                                 HttpServletRequest request) {
        Long uid = auth.getOptionalUserId(request);
        List<CommentRes> content = service.listByPost(postId, uid);
        return ResponseEntity.ok(content);
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

    @Operation(summary = "댓글 수정(작성자만)")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping(value = "/api/comments/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentRes> update(@PathVariable Long id,
                                             @Valid @RequestBody CommentUpdateReq req,
                                             HttpServletRequest request) {
        Long uid = auth.requireUserId(request);   // 로그인 여부 + 유저 ID 얻기
        CommentRes res = service.update(id, req, uid);
        return ResponseEntity.ok(res);
    }

}
