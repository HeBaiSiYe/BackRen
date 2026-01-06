package cn.edu.aynu.siyemanager.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HouseholdListVO {
    private Long id;
    private String householdNo;     // 户号
    private String headName;        // 户主姓名
    private String headIdCard;      // 户主身份证号
    private String province;        // 省
    private String city;            // 市
    private String district;        // 区
    private String detailAddress;   // 详细地址
    private Integer memberCount;    // 家庭成员数量
    private Integer status;         // 状态：1-正常，2-注销
    private LocalDateTime createTime;
}