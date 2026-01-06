package cn.edu.aynu.siyemanager.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PersonListVO {
    private Long id;                    // 人员ID
    private String idCard;              // 身份证号
    private String name;                // 姓名
    private Integer gender;             // 性别：1-男，2-女
    private String nation;              // 民族
    private LocalDate birthday;         // 出生日期
    private Long householdId;           // 所属户ID
    private String relation;            // 与户主关系
    private Integer personType;         // 人口类型：1-户籍地人口，2-流动人口，3-重点人口
    private String registerProvince;    // 户籍省
    private String registerCity;        // 户籍市
    private String registerDistrict;    // 户籍区
    private String registerDetail;      // 户籍详细地址
    private String currentProvince;     // 现住省
    private String currentCity;         // 现住市
    private String currentDistrict;     // 现住区
    private String currentDetail;       // 现住详细地址
    private String phone;               // 联系电话
    private Integer personStatus;       // 人员状态：1-正常，2-死亡
    private LocalDateTime createTime;   // 创建时间
}