package cn.edu.aynu.siyemanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("certificate")
public class Certificate {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long personId;          // 人员ID
    private Integer type;           // 类型：1-身份证，2-居住证
    private String number;          // 证件号码
    private LocalDate issueDate;    // 签发日期
    private LocalDate expireDate;   // 有效期
    private Integer status;         // 状态：1-有效，2-过期，3-挂失

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}