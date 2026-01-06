package cn.edu.aynu.siyemanager.service.impl;

import cn.edu.aynu.siyemanager.entity.User;
import cn.edu.aynu.siyemanager.mapper.UserMapper;
import cn.edu.aynu.siyemanager.service.UserService;
import cn.edu.aynu.siyemanager.vo.UserProfileVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        // 直接调用Mapper的方法
        return userMapper.selectByUsernameAndPassword(username, password);
    }

    @Override
    @Transactional
    public boolean register(User user) {
        // 检查用户名是否已存在
        int count = userMapper.countByUsername(user.getUsername());
        if (count > 0) {
            return false;
        }

        // 设置默认值
        if (user.getRole() == null) {
            user.setRole("OFFICER");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }

        // 插入用户
        int result = userMapper.insert(user);
        return result > 0;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.selectByUsername(username) != null;
    }

    @Override
    @Transactional
    public boolean saveUser(User user) {
        try {
            // 密码不用加密，直接存储
            // 设置默认值
            if (user.getStatus() == null) {
                user.setStatus(1); // 默认启用
            }

            // 保存用户
            int result = userMapper.insert(user);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public UserProfileVO getUserProfile(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserProfileVO vo = new UserProfileVO();
        BeanUtils.copyProperties(user, vo);

        return vo;
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // 1. 参数校验
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("旧密码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("新密码不能为空");
        }
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            throw new IllegalArgumentException("新密码长度必须在6-20个字符之间");
        }

        // 2. 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 验证旧密码
        if (!oldPassword.equals(user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 4. 检查新密码是否与旧密码相同
        if (newPassword.equals(user.getPassword())) {
            throw new RuntimeException("新密码不能与旧密码相同");
        }

        // 5. 更新密码
        int result = userMapper.updatePassword(userId, newPassword);

        return result > 0;
    }
}