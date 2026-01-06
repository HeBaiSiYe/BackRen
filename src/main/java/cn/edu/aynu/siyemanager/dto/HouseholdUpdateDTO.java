package cn.edu.aynu.siyemanager.dto;

import lombok.Data;

@Data
public class HouseholdUpdateDTO {
    private String householdNo;     // 户号
    private String areaCode;        // 区划编码
    private String province;        // 省
    private String city;            // 市
    private String district;        // 区县
    private String detailAddress;   // 详细地址
    private Integer status;         // 状态：1-正常，2-注销
}