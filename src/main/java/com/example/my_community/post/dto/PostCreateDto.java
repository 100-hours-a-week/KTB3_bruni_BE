package com.example.my_community.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 게시물 등록 요청에 대한 dto 클래스
 */
@Getter
public class PostCreateDto {
    @NotBlank(message ="제목은 공백이면 안 됩니다." )
    @Size(min = 1, max = 100)
    private String title; // 게시글 제목

    @NotBlank
    @Size(min = 1, max = 5000)
    private String content; // 게시글 본문


    public PostCreateDto() {} // 역직렬화용 기본 생성자

    public PostCreateDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
