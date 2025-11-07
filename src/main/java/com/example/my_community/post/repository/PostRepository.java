package com.example.my_community.post.repository;

import com.example.my_community.post.domain.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post save(Post post);
    Optional<Post> findById(Long id);
    List<Post> findAll(int page, int size, String sortBy, boolean desc);
    long count();
    void deleteById(Long id);
}
