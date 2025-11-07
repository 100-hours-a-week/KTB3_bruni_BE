package com.example.my_community.post.domain;

import com.example.my_community.comment.domain.Comment;
import com.example.my_community.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Post {
    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String content;

    @Column(name = "author_id") // 외래 키
    private Long authorId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    public Post() {}

    public Post(Long id, String title, String content, Long authorId, OffsetDateTime createdAt, User user, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.user = user;
        this.comments = comments;
    }
}
