package com.solux.sm137.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class WebConfig {

    // 요청 헤더를 처리하는 역할
    // 참고: https://ssnotebook.tistory.com/entry/Spring-Boot-SwaggerOpenAPI-Failed-to-load-remote-configuration-Reverse-Proxy
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}
