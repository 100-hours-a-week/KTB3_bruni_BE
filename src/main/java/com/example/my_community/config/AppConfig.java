package com.example.my_community.config;

import com.example.my_community.auth.Auth;
import com.example.my_community.auth.SessionUser;
import com.example.my_community.comment.repository.CommentRepository;
import com.example.my_community.post.repository.PostRepository;
import com.example.my_community.post.service.PostService;
import com.example.my_community.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // 서비스 계층을 스프링 빈으로 등록
    @Bean
    public PostService postService(PostRepository postRepository,
                                   UserRepository userRepository,
                                   CommentRepository commentRepository) {
        return new PostService(postRepository, userRepository, commentRepository);
    }





    @Bean
    public Auth auth() {
        return new SessionUser();
    }
}
