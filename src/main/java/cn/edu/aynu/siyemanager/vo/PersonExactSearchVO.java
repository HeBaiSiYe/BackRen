package cn.edu.aynu.siyemanager.vo;

import lombok.Data;

@Data
public class PersonExactSearchVO {
    private Long id;
    private String name;
    private Integer gender;
    private String nation;
    private String phone;
    private Integer personType;
    private String registerProvince;
    private String registerCity;
    private String registerDistrict;
    private String registerDetail;
}