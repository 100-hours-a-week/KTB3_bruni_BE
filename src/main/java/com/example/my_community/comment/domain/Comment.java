package com.example.my_community.comment.domain;

import com.example.my_community.post.domain.Post;
import com.example.my_community.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.OffsetDateTime;

@Entity
@Getter
public class Comment {
    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "author_id")
    private Long authorId;

    private String content;
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;


    public Comment() {
    }

    public Comment(Long id, Long postId, Long authorId, String content, OffsetDateTime createdAt, User user, Post post) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = createdAt;
        this.user = user;
        this.post = post;
    }
}
