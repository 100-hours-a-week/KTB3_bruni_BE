package com.example.my_community.comment.repository;

import com.example.my_community.comment.domain.Comment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CommentRepositoryImpl implements CommentRepository{
    private final Map<Long, Comment> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public Comment save(Comment c) {
        if (c.getId() == null) c.setId(seq.incrementAndGet());
        store.put(c.getId(), c);
        return c;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public long countByPostId(Long postId) {
        return store.values().stream().filter(c -> Objects.equals(c.getPostId(), postId)).count();
    }

    @Override
    public List<Comment> findByPostId(Long postId, int page, int size, String sortBy, boolean desc) {
        Comparator<Comment> comp = switch (sortBy) {
            case "createdAt" -> Comparator.comparing(Comment::getCreatedAt);
            default -> Comparator.comparing(Comment::getId);
        };
        if (desc) comp = comp.reversed();

        return store.values().stream()
                .filter(c -> Objects.equals(c.getPostId(), postId))
                .sorted(comp)
                .skip((long) page * size)
                .limit(size)
                .toList();
    }
}
