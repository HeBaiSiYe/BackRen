package cn.edu.aynu.siyemanager.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HouseholdCancelVO {
    private Long id;            // 户籍ID
    private String householdNo; // 户号
    private Integer status;     // 状态：2-注销
    private LocalDateTime createTime; // 创建时间（返回创建时间）
}