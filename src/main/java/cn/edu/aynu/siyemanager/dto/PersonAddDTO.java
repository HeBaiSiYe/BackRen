package cn.edu.aynu.siyemanager.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonAddDTO {
    private String name;                    // 姓名
    private Integer gender;                 // 性别：1-男，2-女
    private String nation;                  // 民族
    private String idCard;                  // 身份证号
    private LocalDate birthday;             // 出生日期

    private Long householdId;               // 所属户ID（可选）
    private String relation;                // 与户主关系（可选）

    private Integer personType = 1;         // 人口类型：1-户籍地人口，2-流动人口，3-重点人口（默认1）

    // 现住地址（可选）
    private String currentProvince;
    private String currentCity;
    private String currentDistrict;
    private String currentDetail;

    private String phone;                   // 联系电话
    private Integer personStatus = 1;       // 人员状态：1-正常，2-死亡（默认1）
}