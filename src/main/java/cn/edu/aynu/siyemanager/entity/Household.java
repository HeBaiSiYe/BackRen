package cn.edu.aynu.siyemanager.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Household {
    private Long id;
    private String householdNo;     // 户号
    private String areaCode;        // 所属区划

    private String province;        // 省
    private String city;            // 市
    private String district;        // 区
    private String detailAddress;   // 详细地址

    private Long headPersonId;      // 户主ID
    private Integer status;         // 状态：1-正常，2-注销

    private LocalDateTime createTime;
}