package cn.edu.aynu.siyemanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HouseholdAddDTO {

    @NotBlank(message = "户号不能为空")
    private String householdNo;     // 户号

    @NotBlank(message = "区划编码不能为空")
    private String areaCode;        // 所属区划

    private String province;        // 省
    private String city;            // 市
    private String district;        // 区

    private String detailAddress;   // 详细地址

    private Long headPersonId;      // 户主ID（注意接口文档中后面有个空格"headPersonId	"）

    private Integer status = 1;     // 状态：1-正常（默认1）
}