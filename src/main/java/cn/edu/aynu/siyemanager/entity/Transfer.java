package cn.edu.aynu.siyemanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("transfer")
public class Transfer {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer type;           // 类型：1-出生申报，2-夫妻投靠，3-子女投靠，4-死亡注销，5-户口迁出
    private Long personId;          // 申请人ID
    private Long targetPersonId;    // 目标人ID
    private String reason;          // 申请原因
    private Integer status;         // 状态：1-待审核，2-通过，3-拒绝
    private Long auditorId;         // 审核人

    private LocalDateTime auditTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}