package com.example.my_community.post.controller;

import com.example.my_community.auth.Auth;
import com.example.my_community.post.dto.PostCreateDto;
import com.example.my_community.post.dto.PostRes;
import com.example.my_community.post.dto.PostUpdateReq;
import com.example.my_community.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * [PostController.class]
 * Controller - post 도메인
 * 클래스 역할 : 사용자의 HTTP 요청을 받아 각 기능(메서드)와 매핑시켜준다.
 * 요청 URI = /api/posts
 * 요청 바디 = 게시글 제목, 게시글 본문
 */
@Tag(name = "Posts", description = "게시글 CRUD API")
@RestController
@RequestMapping(value = "/api/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController {
    private final PostService service; // 서비스 계층 의존
    private final Auth auth;           // 인증을 위한 의존

    public PostController(PostService service, Auth auth) {
        this.service = service;
        this.auth = auth;
    }

    /**
     * [create 메서드] : 사용자로부터 받은 요청(제목, 본문) -> 게시물 서비스 계층 -> Http 응답(바디에 정보 담아서) 생성
     * @param postCreateDto : PostCreateDTO(=게시글 생성 DTO 클래스) 객체
     *  - @Valid : 유효성 검증, @RequestBody : HTTP 요청 바디를 자바 객체로 매핑(주로 Json 형태의 데이터)
     *
     * @param request : HttpServletRequest
     *                - 웹 클라이언트(예: 웹 브라우저)가 서버로 보낸 HTTP 요청에 대한 정보를 담고 있는 객체
     *                - 서버 애플리케이션에서 이 요청 정보(헤더, 쿠키)를 파싱하여 편리하게 사용할 수 있게 해줌
     *
     * @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
     *  - @PostMapping : HTTP POST 메서드로 들어오는 요청과 매핑을 의미
     *  - comsumes : 받아들일 수 있는 요청 본문(바디)의 미디어 타입 지정
     *  - MediaType.APPLICATION_JSON_VALUE : 바디의 Content-Type이 "application/json"이어야 함을 명시
     * @return
     */
    @Operation(summary = "게시글 생성")
    @ApiResponse(responseCode = "201", description = "생성됨")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostRes> create(@Valid @RequestBody PostCreateDto postCreateDto, HttpServletRequest request) {
        Long id = auth.requireUserId(request); // 세션 인증을 통해 id 받기
        PostRes res = service.create(postCreateDto, id);
        return ResponseEntity.created(URI.create("/api/posts/" + res.getId())).body(res);
    }

    // 단건 조회
    @Operation(summary = "게시글 단건 조회")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/{id}")
    public ResponseEntity<PostRes> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    // 페이지 응답 래퍼
    @Schema(name = "PostPageResponse", description = "게시글 페이지 응답")
    public static class PageResponse<T> {
        @Schema(description = "컨텐츠 목록")
        public final List<T> content;
        @Schema(example = "0") public final int page;
        @Schema(example = "10") public final int size;
        @Schema(example = "123") public final long totalElements;
        @Schema(example = "13") public final long totalPages;

        public PageResponse(List<T> content, int page, int size, long totalElements, long totalPages) {
            this.content = content;
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }
    }

    // 목록 조회(페이지)
    @Operation(summary = "게시글 목록 조회(페이지)")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping
    public ResponseEntity<PageResponse<PostRes>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        List<PostRes> content = service.list(page, size, sort, dir);
        long total = service.count();
        long totalPages = (long) Math.ceil((double) total / size);
        return ResponseEntity.ok(new PageResponse<>(content, page, size, total, totalPages));
    }

    // 수정 (작성자만)
    @Operation(summary = "게시글 수정")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping("/{id}")
    public ResponseEntity<PostRes> update(@PathVariable Long id,
                                          @Valid @RequestBody PostUpdateReq req,
                                          HttpServletRequest request) {
        Long uid = auth.requireUserId(request); //
        return ResponseEntity.ok(service.update(id, req, uid));
    }

    // 삭제 (작성자만)
    @Operation(summary = "게시글 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long uid = auth.requireUserId(request);
        service.delete(id, uid);
        return ResponseEntity.noContent().build();
    }

}
