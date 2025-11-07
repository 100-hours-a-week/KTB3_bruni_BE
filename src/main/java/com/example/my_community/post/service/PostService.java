package com.example.my_community.post.service;

import com.example.my_community.common.exception.ForbiddenException;
import com.example.my_community.common.exception.NotFoundException;
import com.example.my_community.common.exception.UnauthorizedException;
import com.example.my_community.post.domain.Post;
import com.example.my_community.post.dto.PostCreateDto;
import com.example.my_community.post.dto.PostRes;
import com.example.my_community.post.dto.PostUpdateReq;
import com.example.my_community.post.repository.PostRepository;

import java.time.OffsetDateTime;
import java.util.List;


public class PostService {
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    // 도메인 -> 응답 DTO 변환
    private PostRes toRes(Post p) {
        return new PostRes(p.getId(), p.getTitle(), p.getContent(), p.getAuthorId(), p.getCreatedAt().toString());
    }

    // 게시글 생성
    public PostRes create(PostCreateDto req, Long currentUserId) {
        if (currentUserId == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Post post = new Post(
                null, // null로 전달하는 이유? 서비스 계층에서 id(식별자)에 대한 연산은 수행하지 않는게 맞고, id 관련 연산은 레포지토리 계층에서 하는게 맞다.
                req.getTitle(),
                req.getContent(),
                currentUserId,
                OffsetDateTime.now(),

        );
        Post saved = repository.save(post);
        return toRes(saved);
    }

    // 단건 조회 (없으면 404)
    public PostRes get(Long id) {
        Post post = repository.findById(id).orElseThrow(() -> new NotFoundException(
                "게시글을 찾을 수 없습니다. id=" + id));
        return toRes(post);
    }

    // 목록 조회 (페이지네이션)
    public List<PostRes> list(int page, int size, String sort, String dir) {
        boolean desc = "desc".equalsIgnoreCase(dir);
        List<Post> items = repository.findAll(page, size, sort, desc);
        return items.stream().map(this::toRes).toList();
    }

    // 총 개수
    public long count() {
        return repository.count();
    }

    // 게시글 수정 (작성자만 가능, 아니면 403)
    public PostRes update(Long id, PostUpdateReq req, Long currentUserId) {
        Post post = repository.findById(id).orElseThrow(() -> new NotFoundException(
                "게시글을 찾을 수 없습니다. id=" + id
        ));
        // id 로 수정 권한 체크
        if (!post.getAuthorId().equals(currentUserId)) {
            throw new ForbiddenException("작성자만 수정 가능합니다.");
        }
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        Post saved = repository.save(post);
        return toRes(saved);
    }

    // 게시글 삭제 (작성자만 가능, 아니면 403)
    public void delete(Long id, Long currentUserId) {
        Post post = repository.findById(id).orElseThrow(() ->
                new NotFoundException("게시글을 찾을 수 없습니다. id=" + id));

        if (!post.getAuthorId().equals(currentUserId)) {
            throw new ForbiddenException("작성자만 삭제 가능합니다.");
        }
        repository.deleteById(id);
    }
}
