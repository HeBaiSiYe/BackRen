package cn.edu.aynu.siyemanager.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProfileVO {
    private Long id;
    private String username;
    private String realName;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
}