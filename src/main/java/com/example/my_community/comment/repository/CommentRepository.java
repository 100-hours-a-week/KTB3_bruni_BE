package com.example.my_community.comment.repository;

import com.example.my_community.comment.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment c);
    Optional<Comment> findById(Long id);
    void deleteById(Long id);
    long countByPostId(Long postId);
    List<Comment> findByPostId(Long postId, int page, int size, String sortBy, boolean desc);
}
