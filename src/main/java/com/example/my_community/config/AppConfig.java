package com.example.my_community.config;

import com.example.my_community.auth.Auth;
import com.example.my_community.auth.SessionUser;
import com.example.my_community.comment.repository.CommentRepository;
import com.example.my_community.comment.repository.CommentRepositoryImpl;
import com.example.my_community.comment.service.CommentService;
import com.example.my_community.post.repository.PostRepositoryImpl;
import com.example.my_community.post.repository.PostRepository;
import com.example.my_community.post.service.PostService;
import com.example.my_community.user.repository.UserRepositoryImpl;
import com.example.my_community.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    // 메모리 저장소를 스프링 빈으로 등록
    @Bean
    public PostRepository postRepository() {
        return new PostRepositoryImpl();
    }

    // 서비스 계층을 스프링 빈으로 등록
    @Bean
    public PostService postService(PostRepository postRepository) {
        return new PostService(postRepository);
    }

    @Bean
    public CommentRepository commentRepository() {
        return new CommentRepositoryImpl();
    }

    @Bean
    public CommentService commentService(CommentRepository commentRepository, PostRepository postRepository) {
        return new CommentService(commentRepository, postRepository);
    }

    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryImpl();
    }

    @Bean
    public Auth auth() {
        return new SessionUser();
    }
}
