package cn.edu.aynu.siyemanager.controller;

import cn.edu.aynu.siyemanager.dto.RegisterDTO;
import cn.edu.aynu.siyemanager.entity.User;
import cn.edu.aynu.siyemanager.service.UserService;
import cn.edu.aynu.siyemanager.utils.JwtUtil;
import cn.edu.aynu.siyemanager.vo.Result;
import cn.edu.aynu.siyemanager.dto.LoginDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            // 检查用户名是否已存在
            if (userService.existsByUsername(registerDTO.getUsername())) {
                return Result.error("用户名已存在");
            }

            // 创建用户实体
            User user = new User();
            user.setUsername(registerDTO.getUsername());
            user.setPassword(registerDTO.getPassword()); // Service 层会加密
            user.setRealName(registerDTO.getRealName());
            user.setRole(registerDTO.getRole());
            user.setStatus(1); // 默认启用

            // 保存用户
            boolean success = userService.saveUser(user);

            if (success) {
                // 返回注册成功的信息
                Map<String, Object> data = new HashMap<>();
                data.put("id", user.getId());
                data.put("username", user.getUsername());

                return Result.success("注册成功", data);
            } else {
                return Result.error("注册失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("注册失败：" + e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            // 使用 login 方法验证用户名和密码
            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());

            if (user == null) {
                return Result.error("用户名或密码错误");
            }

            if (user.getStatus() == 0) {
                return Result.error("用户已被禁用");
            }

            // 生成token
            String token = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getId(),
                    user.getRole(),
                    user.getRealName()
            );

            // 返回用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("role", user.getRole());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userInfo", userInfo);

            return Result.success("登录成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 用户退出登录
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public Result logout(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "退出成功");
            data.put("timestamp", System.currentTimeMillis());

            return Result.success(data);

        } catch (Exception e) {
            return Result.success("退出成功");
        }
    }
}