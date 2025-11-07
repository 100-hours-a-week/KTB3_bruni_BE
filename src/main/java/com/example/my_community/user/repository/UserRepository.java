package com.example.my_community.user.repository;

import com.example.my_community.user.domain.User;

import java.util.Optional;

public interface UserRepository {
    User save(User u);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
}
