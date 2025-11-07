package com.example.my_community.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI myApi() {
        return new OpenAPI().info(new Info()
                .title("My Community API")
                .version("v1")
                .description("세션 기반 인증/게시글/댓글 API"));
    }

    // 필요한 경우 도메인별 그룹 탭 생성
    @Bean
    public GroupedOpenApi authGroup() {
        return GroupedOpenApi.builder()
                .group("Auth")
                .packagesToScan("com.example.my_community.auth.controller")
                .build();
    }
    @Bean
    public GroupedOpenApi postGroup() {
        return GroupedOpenApi.builder()
                .group("Post")
                .packagesToScan("com.example.my_community.post.controller")
                .build();
    }
}
