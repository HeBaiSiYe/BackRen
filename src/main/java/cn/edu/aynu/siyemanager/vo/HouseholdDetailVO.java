package cn.edu.aynu.siyemanager.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HouseholdDetailVO {
    private Long id;                    // 户籍ID
    private String householdNo;         // 户号
    private String areaCode;            // 区划编码
    private String province;            // 省
    private String city;                // 市
    private String district;            // 区
    private String detailAddress;       // 详细地址
    private Long headPersonId;          // 户主ID
    private String headName;            // 户主姓名
    private String headIdCard;          // 户主身份证号
    private Integer status;             // 状态：1-正常，2-注销
    private LocalDateTime createTime;   // 创建时间
    private List<HouseholdMemberVO> members;  // 家庭成员列表
}