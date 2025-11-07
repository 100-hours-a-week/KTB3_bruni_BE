package com.example.my_community.auth;

import jakarta.servlet.http.HttpSession;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface Auth {

    Optional<Long> currentUserId(HttpServletRequest request);

    Long requireUserId(HttpServletRequest request);
}
