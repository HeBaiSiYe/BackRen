package cn.edu.aynu.siyemanager.controller;

import cn.edu.aynu.siyemanager.service.UserService;
import cn.edu.aynu.siyemanager.vo.UserProfileVO;
import cn.edu.aynu.siyemanager.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<UserProfileVO> getCurrentUserProfile(HttpServletRequest request) {
        try {
            // 从request属性中获取用户ID（在JwtInterceptor中设置的）
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error(401, "用户未登录");
            }

            // 转换为Long类型
            Long userId;
            if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            } else if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof String) {
                userId = Long.valueOf((String) userIdObj);
            } else {
                return Result.error("用户ID格式错误");
            }

            UserProfileVO profile = userService.getUserProfile(userId);
            return Result.success(profile);

        } catch (NumberFormatException e) {
            return Result.error("用户ID格式错误");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("获取用户信息失败");
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/change-password")
    public Result<String> changePassword(@RequestBody ChangePasswordRequest request,
                                         HttpServletRequest httpRequest) {
        try {
            // 1. 参数验证
            if (!StringUtils.hasText(request.getOldPassword())) {
                return Result.error("旧密码不能为空");
            }
            if (!StringUtils.hasText(request.getNewPassword())) {
                return Result.error("新密码不能为空");
            }

            // 2. 获取当前用户ID
            Object userIdObj = httpRequest.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error(401, "用户未登录");
            }

            Long userId = Long.valueOf(userIdObj.toString());

            // 3. 调用Service修改密码
            boolean success = userService.changePassword(
                    userId,
                    request.getOldPassword().trim(),
                    request.getNewPassword().trim()
            );

            if (success) {
                return Result.success("密码修改成功");
            } else {
                return Result.error("密码修改失败");
            }

        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error("密码修改失败");
        }
    }

    /**
     * 内部类：用于接收请求参数
     */
    private static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;

        // Getter和Setter
        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}