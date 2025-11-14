package com.example.my_community.post.service;

import com.example.my_community.common.exception.ForbiddenException;
import com.example.my_community.common.exception.NotFoundException;
import com.example.my_community.common.exception.UnauthorizedException;
import com.example.my_community.post.domain.Post;
import com.example.my_community.post.dto.PostCreateDto;
import com.example.my_community.post.dto.PostRes;
import com.example.my_community.post.dto.PostUpdateReq;
import com.example.my_community.post.repository.PostRepository;
import com.example.my_community.user.domain.User;
import com.example.my_community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("post not found"));
    }


    // 도메인 -> 응답 DTO 변환
    private PostRes toRes(Post p) {
        return new PostRes(p.getId(), p.getTitle(), p.getContent(),
                p.getUser().getId(), p.getCreatedAt().toString(), p.getLikeCount());
    }

    // 게시글 생성
    @Transactional
    public PostRes create(PostCreateDto req, Long currentUserId) {
        if (currentUserId == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        Post post = new Post(
                null, // null로 전달하는 이유? 서비스 계층에서 id(식별자)에 대한 연산은 수행하지 않는게 맞고, id 관련 연산은 레포지토리 계층에서 하는게 맞다.
                userRepository.findById(currentUserId).get(),
                req.getTitle(),
                req.getContent(),
                OffsetDateTime.now(),
                0
        );
        Post saved = postRepository.save(post);
        return toRes(saved);
    }

    // 단건 조회 (없으면 404)
    public PostRes get(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "게시글을 찾을 수 없습니다. id=" + id));
        return toRes(post);
    }

    // 목록 조회 (페이지네이션)
    public List<PostRes> list(int page, int size, String sort, String dir) {
        boolean desc = "desc".equalsIgnoreCase(dir);
        List<Post> items = postRepository.findAll();
        return items.stream().map(this::toRes).toList();
    }


    // 게시글 수정 (작성자만 가능, 아니면 403)
    @Transactional
    public PostRes update(Long id, PostUpdateReq req, Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "게시글을 찾을 수 없습니다. id=" + id
        ));
        // id 로 수정 권한 체크
        if (!post.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("작성자만 수정 가능합니다.");
        }
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        Post saved = postRepository.save(post);
        return toRes(saved);
    }

    // 게시글 삭제 (작성자만 가능, 아니면 403)
    @Transactional
    public void delete(Long id, Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new NotFoundException("게시글을 찾을 수 없습니다. id=" + id));

        if (!post.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("작성자만 삭제 가능합니다.");
        }
        postRepository.deleteById(id);
    }

    // 총 개수
    public long count() {
        return postRepository.count();
    }
}
