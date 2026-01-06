package cn.edu.aynu.siyemanager.vo;

import lombok.Data;

@Data
public class PersonAddVO {
    private Long id;            // 新增人员的ID
    private String name;        // 姓名
    private String idCard;      // 身份证号
}