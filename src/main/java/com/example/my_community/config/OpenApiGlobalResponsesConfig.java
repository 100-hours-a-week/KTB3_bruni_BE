package com.example.my_community.config;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiGlobalResponsesConfig {

    private ApiResponse jsonError(String description) {
        Schema<?> schemaRef = new Schema<>().$ref("#/components/schemas/ErrorResponse");
        MediaType mt = new MediaType().schema(schemaRef);
        Content content = new Content().addMediaType("application/json", mt);
        return new ApiResponse().description(description).content(content);
    }

    @Bean
    public OpenApiCustomizer addGlobalErrorResponses() {
        // 상태 코드 별 설명
        Map<String, ApiResponse> toAdd = Map.of(
                "400", jsonError("검증 실패"),
                "401", jsonError("인증 실패"),
                "403", jsonError("권한 없음"),
                "404", jsonError("리소스를 찾을 수 없음"),
                "500", jsonError("서버 오류")
        );

        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(op -> {
                    // 이미 명시된 응답은 덮어쓰지 않음
                    toAdd.forEach((code, resp) -> {
                        if (op.getResponses() == null || op.getResponses().get(code) == null) {
                            op.getResponses().addApiResponse(code, resp);
                        }
                    });
                })
        );
    }
}
