package com.example.my_community.comment.service;

import com.example.my_community.comment.domain.Comment;
import com.example.my_community.comment.dto.CommentCreateReq;
import com.example.my_community.comment.dto.CommentRes;
import com.example.my_community.comment.dto.CommentUpdateReq;
import com.example.my_community.comment.repository.CommentRepository;
import com.example.my_community.common.exception.ForbiddenException;
import com.example.my_community.common.exception.NotFoundException;
import com.example.my_community.post.domain.Post;
import com.example.my_community.post.repository.PostRepository;
import com.example.my_community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Transactional
    public CommentRes create(Long postId, CommentCreateReq req, Long currentUserId) {
        // 게시글 존재 확인
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new NotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        // 작성자 조회
        var user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. id=" + currentUserId));

        Comment c = new Comment(
                post,
                user,
                req.getContent()
        );
        Comment saved = commentRepository.save(c);
        return toRes(saved, currentUserId);
    }

    /** 게시글별 댓글 목록 */
    public List<CommentRes> listByPost(Long postId, Long currentUserId) {
        // 게시글 존재 확인 (없는 postId일 때 404)
        postRepository.findById(postId).orElseThrow(() ->
                new NotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        List<Comment> comments =
                commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        return comments.stream()
                .map(c -> toRes(c, currentUserId))
                .toList();
    }

    public long countByPost(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    @Transactional
    public void delete(Long commentId, Long currentUserId) {
        Comment c = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("댓글을 찾을 수 없습니다. id=" + commentId));
        if (!c.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("작성자만 삭제할 수 있습니다.");
        }
        commentRepository.deleteById(commentId);
    }

    private CommentRes toRes(Comment c, Long currentUserId) {
        String created = (c.getCreatedAt() != null) ? ISO.format(c.getCreatedAt()) : null;
        String nickname = (c.getUser() != null) ? c.getUser().getNickname() : null;
        boolean mine = currentUserId != null
                && c.getUser() != null
                && c.getUser().getId().equals(currentUserId);
        return new CommentRes(
                c.getId(),
                c.getPost().getId(),
                c.getUser().getId(),
                nickname,
                c.getContent(),
                created,
                mine
        );
    }

    @Transactional
    public CommentRes update(Long commentId, CommentUpdateReq req, Long currentUserId) {
        // 1) 댓글 존재 여부 확인
        Comment c = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("댓글을 찾을 수 없습니다. id=" + commentId));

        // 2) 작성자 권한 체크
        if (!c.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("작성자만 수정할 수 있습니다.");
        }

        // 3) 내용 수정
        c.changeContent(req.getContent());   // @Setter 있어서 사용 가능
        // JPA 변경감지로 자동 업데이트 되지만, 네 스타일에 맞춰 명시적으로 save 해도 됨
        Comment saved = commentRepository.save(c);

        // 4) 수정된 결과 반환
        return toRes(saved, currentUserId);
    }

}
