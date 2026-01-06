package cn.edu.aynu.siyemanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/login", "/auth/register", "/error");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 开发环境完全放开
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 使用 allowedOriginPatterns 支持通配符
                .allowedMethods("*")         // 允许所有方法
                .allowedHeaders("*")         // 允许所有头
                .allowCredentials(true)      // 允许凭证
                .maxAge(3600);               // 预检请求缓存时间
    }
}