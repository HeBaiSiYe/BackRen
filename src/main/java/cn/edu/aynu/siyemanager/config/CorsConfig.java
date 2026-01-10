package cn.edu.aynu.siyemanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. 允许所有本地局域网IP（推荐开发环境用）
        config.addAllowedOriginPattern("*"); // Spring Boot 2.4.0+ 使用

        // 或者指定具体IP段（更安全）
        // config.addAllowedOriginPattern("http://192.168.*.*:*");
        // config.addAllowedOriginPattern("http://10.*.*.*:*");
        // config.addAllowedOriginPattern("http://172.16.*.*:*");

        // 或者添加具体的IP地址
        // config.addAllowedOrigin("http://192.168.1.100:3000");
        // config.addAllowedOrigin("http://192.168.1.100:8080");

        // 2. 允许本地开发
        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://127.0.0.1:8081");
        config.addAllowedOrigin("http://127.0.0.1:5173");

        // 3. 允许的方法和请求头
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        // 4. 允许携带凭证（cookies、认证信息）
        config.setAllowCredentials(true);

        // 5. 设置预检请求缓存时间（单位：秒）
        config.setMaxAge(3600L);

        // 6. 暴露响应头（如果需要）
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Disposition");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}