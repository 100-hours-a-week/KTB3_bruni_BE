package com.example.my_community.comment.dto;

public class CommentRes {
    private final Long id;
    private final Long postId;
    private final Long authorId;
    private final String content;
    private final String createdAt;

    public CommentRes(Long id, Long postId, Long authorId, String content, String createdAt) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
