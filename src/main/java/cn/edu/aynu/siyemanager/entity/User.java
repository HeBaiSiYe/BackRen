package cn.edu.aynu.siyemanager.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String realName;  // 数据库字段 real_name
    private String role;
    private Integer status;
    private LocalDateTime createTime;
}