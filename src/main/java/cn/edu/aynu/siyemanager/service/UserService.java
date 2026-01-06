package cn.edu.aynu.siyemanager.service;

import cn.edu.aynu.siyemanager.entity.User;
import cn.edu.aynu.siyemanager.vo.UserProfileVO;

public interface UserService {
    User login(String username, String password);
    boolean register(User user);
    User getUserById(Long id);
    User getUserByUsername(String username);
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 保存用户（新增或更新）
     */
    boolean saveUser(User user);

    /**
     * 获取当前用户信息
     * @param userId 当前用户ID（从JWT token中获取）
     */
    UserProfileVO getUserProfile(Long userId);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}