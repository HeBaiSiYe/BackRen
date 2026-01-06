package cn.edu.aynu.siyemanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("person")
public class Person {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String idCard;          // 身份证号
    private String name;
    private Integer gender;         // 性别：1-男，2-女
    private String nation;          // 民族
    private LocalDate birthday;     // 出生日期

    private Long householdId;       // 所属户ID
    private String relation;        // 与户主关系
    private Integer personType;     // 人口类型：1-户籍地人口，2-流动人口，3-重点人口

    // 户籍地址
    private String registerProvince;
    private String registerCity;
    private String registerDistrict;
    private String registerDetail;

    // 现住地址
    private String currentProvince;
    private String currentCity;
    private String currentDistrict;
    private String currentDetail;

    private String phone;
    private Integer personStatus;   // 人员状态：1-正常，2-死亡

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Long householdId) {
        this.householdId = householdId;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public Integer getPersonType() {
        return personType;
    }

    public void setPersonType(Integer personType) {
        this.personType = personType;
    }

    public String getRegisterProvince() {
        return registerProvince;
    }

    public void setRegisterProvince(String registerProvince) {
        this.registerProvince = registerProvince;
    }

    public String getRegisterCity() {
        return registerCity;
    }

    public void setRegisterCity(String registerCity) {
        this.registerCity = registerCity;
    }

    public String getRegisterDistrict() {
        return registerDistrict;
    }

    public void setRegisterDistrict(String registerDistrict) {
        this.registerDistrict = registerDistrict;
    }

    public String getRegisterDetail() {
        return registerDetail;
    }

    public void setRegisterDetail(String registerDetail) {
        this.registerDetail = registerDetail;
    }

    public String getCurrentProvince() {
        return currentProvince;
    }

    public void setCurrentProvince(String currentProvince) {
        this.currentProvince = currentProvince;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getCurrentDistrict() {
        return currentDistrict;
    }

    public void setCurrentDistrict(String currentDistrict) {
        this.currentDistrict = currentDistrict;
    }

    public String getCurrentDetail() {
        return currentDetail;
    }

    public void setCurrentDetail(String currentDetail) {
        this.currentDetail = currentDetail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPersonStatus() {
        return personStatus;
    }

    public void setPersonStatus(Integer personStatus) {
        this.personStatus = personStatus;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}