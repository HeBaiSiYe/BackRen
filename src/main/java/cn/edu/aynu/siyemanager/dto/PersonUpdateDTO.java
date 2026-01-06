package cn.edu.aynu.siyemanager.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PersonUpdateDTO {
    private String name;
    private Integer gender;
    private String nation;
    private LocalDate birthday;
    private Long householdId;
    private String relation;
    private Integer personType;
    private String currentProvince;
    private String currentCity;
    private String currentDistrict;
    private String currentDetail;
    private String phone;
    private Integer personStatus;
}