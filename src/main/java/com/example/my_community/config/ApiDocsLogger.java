package com.example.my_community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ApiDocsLogger implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(ApiDocsLogger.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        var ctx = event.getApplicationContext();
        if (!(ctx instanceof WebServerApplicationContext wsCtx)) return;

        Environment env = wsCtx.getEnvironment();

        // 1) 호스트/포트/프로토콜
        boolean ssl = Boolean.parseBoolean(env.getProperty("server.ssl.enabled", "false"));
        String scheme = ssl ? "https" : "http";
        String host   = env.getProperty("server.address", "localhost");
        int port      = wsCtx.getWebServer().getPort(); // 0(랜덤 포트) 설정해도 실제 바인딩 포트로 찍힘

        // 2) 컨텍스트 패스 & springdoc 경로(기본값 포함)
        String contextPath   = nvl(env.getProperty("server.servlet.context-path"), "");
        String apiDocsPath   = env.getProperty("springdoc.api-docs.path", "/v3/api-docs");
        String swaggerUiPath = env.getProperty("springdoc.swagger-ui.path", "/swagger-ui");

        // 3) URL 조립 (중복 슬래시 방지)
        String base = scheme + "://" + host + ":" + port + normalize(contextPath);

        String apiDocsUrl = base + normalize(apiDocsPath);
        String uiUrl      = base + normalize(swaggerUiPath);

        // 4) 로그 출력
        log.info("""
                ----------------------------------------------------------
                API documentation is available:
                 • OpenAPI JSON : {}
                 • Swagger UI   : {} \s
                ----------------------------------------------------------""",
                apiDocsUrl, uiUrl);
    }

    private static String nvl(String v, String def) { return (v == null) ? def : v; }

    private static String normalize(String p) {
        if (p == null || p.isEmpty() || p.equals("/")) return "";
        return (p.startsWith("/") ? "" : "/") + (p.endsWith("/") ? p.substring(0, p.length()-1) : p);
    }
}
