package cn.edu.aynu.siyemanager.config;

import cn.edu.aynu.siyemanager.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 从请求头中获取token
        String authHeader = request.getHeader("Authorization");

        // 如果请求路径是登录或注册，直接放行
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/auth/login") ||
                requestURI.contains("/auth/register")) {
            return true;
        }

        // 验证token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未授权访问\"}");
            return false;
        }

        String token = authHeader.substring(7); // 去除Bearer前缀

        if (!jwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"令牌无效或已过期\"}");
            return false;
        }

        // 将用户信息存入request，方便后续使用
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            request.setAttribute("username", username);
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);
        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"令牌解析错误\"}");
            return false;
        }

        return true;
    }
}