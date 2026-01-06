package cn.edu.aynu.siyemanager.mapper;

import cn.edu.aynu.siyemanager.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     */
    User selectById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据用户名和密码查询用户（登录用）
     */
    User selectByUsernameAndPassword(
            @Param("username") String username,
            @Param("password") String password);

    /**
     * 查询所有用户
     */
    List<User> selectAll();

    /**
     * 删除用户
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据用户名检查用户是否存在
     */
    int countByUsername(@Param("username") String username);

    @Insert("INSERT INTO user (username, password, real_name, role, status, create_time) " +
            "VALUES (#{username}, #{password}, #{realName}, #{role}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    int update(User user);

    /**
     * 更新密码
     */
    @Update("UPDATE user SET password = #{password} WHERE id = #{userId}")
    int updatePassword(@Param("userId") Long userId, @Param("password") String password);
}