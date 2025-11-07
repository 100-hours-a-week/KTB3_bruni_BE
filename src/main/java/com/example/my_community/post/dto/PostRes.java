package com.example.my_community.post.dto;

/**
 * 게시물관련 응답 dto 클래스
 */
public class PostRes {
    private final Long id;
    private final String title;
    private final String content;
    private final Long authorId;
    private final String createdAt;

    public PostRes(Long id, String title, String content, Long authorId, String createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.createdAt = createdAt;
    }

    // getters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
