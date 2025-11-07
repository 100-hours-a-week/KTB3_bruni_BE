package com.example.my_community.user.domain;

import com.example.my_community.comment.domain.Comment;
import com.example.my_community.post.domain.Post;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class User {
    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String username;
    private String password; // 예: "{noop}pass1" (추후 BCrypt로 교체)

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    public User() {}

    public User(Long id, String username, String password, List<Post> posts, List<Comment> comments) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.posts = posts;
        this.comments = comments;
    }
}
