package com.example.my_community.user.repository;

import com.example.my_community.user.domain.User;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserRepositoryImpl implements UserRepository{
    private final Map<Long, User> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0); // seq : Atomic 타입으로 원자성 보장

    @Override
    public User save(User u) {
        if (u.getId() == null) u.setId(seq.incrementAndGet());
        store.put(u.getId(), u);
        return u;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return store.values().stream().filter(u -> Objects.equals(u.getUsername(), username)).findFirst();
    }
}
