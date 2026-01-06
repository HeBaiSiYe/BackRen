package cn.edu.aynu.siyemanager.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HouseholdUpdateVO {
    private Long id;            // 户籍ID
    private String householdNo; // 户号
    private LocalDateTime updateTime; // 更新时间
}