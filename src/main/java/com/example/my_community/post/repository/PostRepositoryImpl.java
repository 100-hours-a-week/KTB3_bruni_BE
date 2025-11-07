package com.example.my_community.post.repository;

import com.example.my_community.post.domain.Post;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepositoryImpl implements PostRepository{
    private final Map<Long, Post> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    // 게시글 저장
    @Override
    public Post save(Post post) {
        if (post.getId() == null) {
            post.setId(seq.incrementAndGet());
        }
        store.put(post.getId(), post);
        return post;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Post> findAll(int page, int size, String sortBy, boolean desc) {
        Comparator<Post> comp = switch (sortBy) {
            case "createdAt" -> Comparator.comparing(Post::getCreatedAt);
            case "title"     -> Comparator.comparing(Post::getTitle, String.CASE_INSENSITIVE_ORDER);
            default          -> Comparator.comparing(Post::getId);
        };
        if (desc) comp = comp.reversed();

        return store.values().stream()
                .sorted(comp)
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    @Override
    public long count() {
        return store.size();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
