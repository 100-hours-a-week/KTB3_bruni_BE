package com.example.my_community.post.service;

import com.example.my_community.comment.repository.CommentRepository;
import com.example.my_community.common.exception.FileInputException;
import com.example.my_community.common.exception.ForbiddenException;
import com.example.my_community.common.exception.NotFoundException;
import com.example.my_community.common.exception.UnauthorizedException;
import com.example.my_community.post.domain.Post;
import com.example.my_community.post.dto.PostCreateRequest;
import com.example.my_community.post.dto.PostRes;
import com.example.my_community.post.dto.PostUpdateReq;
import com.example.my_community.post.repository.PostRepository;
import com.example.my_community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // 게시글 생성 (제목, 내용, 이미지)
    @Transactional
    public PostRes create(Long currentUserId, PostCreateRequest req) {
        if (currentUserId == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        var user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));

        // 이미지 파일 -> byte[] 변환
        byte[] imageBytes = null;
        MultipartFile image = req.getImage();
        if (image != null && !image.isEmpty()) {
            try {
                imageBytes = image.getBytes();
            } catch (IOException e) {
                throw new FileInputException("게시글 이미지를 처리하는 중 오류가 발생했습니다.", e);
            }
        }

        Post post = new Post(user, req.getTitle(), req.getContent(), imageBytes);
        Post saved = postRepository.save(post);

        return toRes(saved);
    }

    // postId로 게시물 찾기
    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }


    // Post 응답 DTO 변환
    private PostRes toRes(Post p) {
        // null-safe 하게 방어 코드 추가
        Long authorId = (p.getAuthor() != null)
                ? p.getAuthor().getId()
                : null;
        String createdAt = (p.getCreatedAt() != null)
                ? p.getCreatedAt().toString()
                : "";
        String authorNickname = (p.getAuthor() != null)
                ? p.getAuthor().getNickname()
                : null;


        int likeCount = p.getLikeCount();
        int viewCount = p.getViewCount();
        long commentCountLong = commentRepository.countByPostId(p.getId());
        int commentCount = (int) commentCountLong;

        return new PostRes(
                p.getId(),
                p.getTitle(),
                p.getContent(),
                authorId,
                authorNickname,
                createdAt,
                likeCount,
                viewCount,
                commentCount
        );
    }



    // 단건 조회 (+ 조회수 증가)
    public PostRes getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "게시글을 찾을 수 없습니다. id=" + id));

        // 조회수 + 1
        post.increaseViewCount();

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
    public PostRes update(Long postId, PostUpdateReq req, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        // 작성자 권한 체크
        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new ForbiddenException("작성자만 수정 가능합니다.");
        }

        // 제목/내용 수정
        post.changeTitle(req.getTitle());
        post.changeContent(req.getContent());

        // 이미지 수정 (선택)
        MultipartFile imageFile = req.getImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                byte[] bytes = imageFile.getBytes();
                post.changeImage(bytes);       // 기존 이미지 덮어쓰기
            } catch (IOException e) {
                throw new FileInputException("게시글 이미지를 처리하는 중 오류가 발생했습니다.", e);
            }
        }
        // 이미지 파일을 아예 안 보내면 기존 이미지 유지

        Post saved = postRepository.save(post);
        return toRes(saved);
    }




    // 게시글 삭제 (작성자만 가능, 아니면 403)
    @Transactional
    public void delete(Long id, Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new NotFoundException("게시글을 찾을 수 없습니다. id=" + id));

        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new ForbiddenException("작성자만 삭제 가능합니다.");
        }
        // 게시글 삭제 시 관련된(게시글 id를 가진) 댓글들을 먼저 삭제
        commentRepository.deleteCommentsByPostId(id);
        postRepository.deleteById(id);
    }

    // 게시글 총 개수
    public long count() {
        return postRepository.count();
    }

    @Transactional
    public int increaseLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        post.increaseLikeCount();  // 엔티티 메서드로 위임 (아래에서 추가할 거)
        return post.getLikeCount();
    }

    @Transactional
    public int decreaseLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        post.decreaseLikeCount();  // 0 이하로 내려가지 않게 방어 로직 포함
        return post.getLikeCount();
    }



}
