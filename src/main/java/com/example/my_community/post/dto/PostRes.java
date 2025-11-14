package com.example.my_community.post.dto;

import lombok.Data;
import lombok.Getter;

/**
 * 게시물관련 응답 dto 클래스
 */
@Data
public class PostRes {
    private final Long id;
    private final String title;
    private final String content;
    private final Long authorId;
    private final String createdAt;
    private final int likeCount;

    public PostRes(Long id, String title, String content, Long authorId, String createdAt, int likeCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
    }
}
