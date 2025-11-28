package com.example.my_community.post.controller;

import com.example.my_community.auth.Auth;
import com.example.my_community.common.exception.UnauthorizedException;
import com.example.my_community.post.domain.Post;
import com.example.my_community.post.dto.PostCreateRequest;
import com.example.my_community.post.dto.PostRes;
import com.example.my_community.post.dto.PostUpdateReq;
import com.example.my_community.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
@RequiredArgsConstructor

public class PostController {
    private final PostService service; // 서비스 계층 의존
    private final Auth auth;
    /**
     * [create 메서드] : 사용자로부터 받은 요청(제목, 본문) -> 게시물 서비스 계층 -> Http 응답(바디에 정보 담아서) 생성
     * @param req : PostCreateDTO(=게시글 생성 DTO 클래스) 객체
     *  - @Valid : 유효성 검증, @RequestBody : HTTP 요청 바디를 자바 객체로 매핑(주로 Json 형태의 데이터)
     *
     * @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
     *  - @PostMapping : HTTP POST 메서드로 들어오는 요청과 매핑을 의미
     *  - comsumes : 받아들일 수 있는 요청 본문(바디)의 미디어 타입 지정
     *  - MediaType.APPLICATION_JSON_VALUE : 바디의 Content-Type이 "application/json"이어야 함을 명시
     *
     */
    @Operation(summary = "게시글 생성")
    @ApiResponse(responseCode = "201", description = "생성됨")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostRes> create(
            @ModelAttribute PostCreateRequest req
    ) {
        Long loginUserId = auth.requireUserId();
        if (loginUserId == null) {
            throw new UnauthorizedException("게시글 작성을 위해서는 로그인이 필요합니다.");
        }

        PostRes res = service.create(loginUserId, req);
        return ResponseEntity.created(URI.create("/api/posts/" + res.getId())).body(res);
    }

    @Operation(summary = "게시글 단건 조회")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/{id}")
    public ResponseEntity<PostRes> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPost(id));
    }

    @Operation(summary = "게시글 이미지 조회")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long id) {
        Post post = service.findById(id);
        byte[] image = post.getImage();

        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    // 페이지 응답 래퍼
    @Schema(name = "PostPageResponse", description = "게시글 페이지 응답")
    public static class PageResponse<T> {
        @Schema(description = "컨텐츠 목록") public final List<T> content;
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


    @Operation(summary = "게시글 수정")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostRes> update(@PathVariable Long id, @Valid @ModelAttribute PostUpdateReq req) {
        Long loginUserId = auth.requireUserId();
        if (loginUserId == null) {
            throw new UnauthorizedException("게시글 수정을 위해서는 로그인이 필요합니다.");
        }

        PostRes res = service.update(id, req, loginUserId);
        return ResponseEntity.ok(res);
    }




    @Operation(summary = "게시글 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long loginUserId = auth.requireUserId();
        if (loginUserId == null) {
            throw new UnauthorizedException("게시글 삭제를 위해서는 로그인이 필요합니다.");
        }

        service.delete(id, loginUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 좋아요 +1")
    @ApiResponse(responseCode = "200", description = "성공")
    @PostMapping("/{id}/like")
    public ResponseEntity<LikeResponse> like(@PathVariable Long id) {
        Long loginUserId = auth.requireUserId();
        if (loginUserId == null) {
            throw new UnauthorizedException("좋아요를 누르려면 로그인이 필요합니다.");
        }

        int likeCount = service.increaseLikeCount(id);
        return ResponseEntity.ok(new LikeResponse(likeCount));
    }

    @Operation(summary = "게시글 좋아요 -1 (취소)")
    @ApiResponse(responseCode = "200", description = "성공")
    @DeleteMapping("/{id}/like")
    public ResponseEntity<LikeResponse> unlike(@PathVariable Long id) {
        Long loginUserId = auth.requireUserId();
        if (loginUserId == null) {
            throw new UnauthorizedException("좋아요를 취소하려면 로그인이 필요합니다.");
        }

        int likeCount = service.decreaseLikeCount(id);
        return ResponseEntity.ok(new LikeResponse(likeCount));
    }

    /**
     * 좋아요 응답 DTO (필요한 정보만 최소한으로)
     */
    public record LikeResponse(int likeCount) {}


}
