package com.demo.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.List;

/**
 * config CORS
 * - CORS (Cross-Origin Resource Sharing) là một cơ chế bảo mật được triển khai bởi trình duyệt web
 * để kiểm soát cách các tài nguyên từ một miền (origin) được yêu cầu bởi một ứng dụng web đang chạy ở một miền khác.
 * => thiết lập CORS cho phép các ứng dụng web giao tiếp với các API bên ngoài một cách an toàn và hiệu quả.
 */

@Configuration // C1, C2
//@Component // C3
public class AppConfig {

    /**
     *  C1.1: implements WebMvcConfigurer (chỉ sử dụng được cho WebMvcConfigurer)
     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowCredentials(true)
//                .allowedOrigins("http://localhost:5137")
//                .allowedMethods("*")
//                .allowedHeaders("*");
//    }

    /**
     * C1.2: khởi tạo Bean (có thể khởi tạo ra nhiều bean khác thay vì chỉ sử dụng WebMvcConfigurer như C1)
     */
    @Bean
    public WebMvcConfigurer corsFilter() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5137")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(36000);
            }
        };
    }

    /**
     * C2: Sử dụng CorsFilter
     */
//    @Bean
//    public FilterRegistrationBean<CorsFilter> corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("http://localhost:5137"); // đối với 1 domain
//        config.setAllowedOrigins(List.of("http://localhost:5137"));
//        config.addAllowedMethod("*"); // accept các method nào ? (* <=> all)
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
//        config.addAllowedHeader("*"); // accept cho các header nào ? <=> (* <=> all)
//        config.setAllowedHeaders(List.of("*"));
//        source.registerCorsConfiguration("/**", config);
//        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
//        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return bean;
//    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//        response.setHeader("Access-Control-Allow-Origin", "http//localhost:5137");
//        response.setHeader("Access-Control-Allow-Methods", "*");
//        response.setHeader("Access-Control-Allow-Headers", "*");
//        filterChain.doFilter(request, response);
//    }
}
