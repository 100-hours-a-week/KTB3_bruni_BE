package com.example.my_community.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentCreateReq {
    @NotBlank
    @Size(min = 1, max = 1000)
    private String content;

    public CommentCreateReq() {
    }

    public CommentCreateReq(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
