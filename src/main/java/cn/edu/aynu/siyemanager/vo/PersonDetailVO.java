package cn.edu.aynu.siyemanager.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonDetailVO {
    private Long id;            // 人员ID
    private String name;        // 姓名
    private String idCard;      // 身份证号
    private Integer gender;     // 性别：1-男，2-女
    private LocalDate birthday; // 出生日期
    private String phone;       // 联系电话
    private Integer personType; // 人口类型：1-户籍地人口，2-流动人口，3-重点人口
    private Long householdId;   // 所属户ID
    private String householdNo; // 户号
    private String relation;    // 与户主关系
}