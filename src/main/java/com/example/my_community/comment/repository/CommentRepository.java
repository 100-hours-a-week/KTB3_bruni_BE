package com.example.my_community.comment.repository;

import com.example.my_community.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 게시글 기준 댓글 목록 조회 (작성일 오름차순)
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    // 게시글별 댓글 개수 (필요하면 사용)
    long countByPostId(Long postId);

    // "post_id" 컬럼 값이 일치하는 엔티티 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.post.id = :postId")
    void deleteCommentsByPostId(@Param("postId") Long id);
}
