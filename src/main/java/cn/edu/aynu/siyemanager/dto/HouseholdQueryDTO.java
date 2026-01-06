package cn.edu.aynu.siyemanager.dto;

import lombok.Data;

@Data
public class HouseholdQueryDTO {
    // 分页参数
    private Integer page = 1;
    private Integer pageSize = 10;

    // 查询条件
    private String householdNo;     // 户号
    private String headName;        // 户主姓名
    private String province;
    private String city;
    private String district;
    private Integer status;         // 状态：1-正常，2-迁出

    /**
     * 计算偏移量（不存数据库，仅用于计算）
     */
    public Integer getOffset() {
        if (page == null || pageSize == null) {
            return 0;
        }
        return (page - 1) * pageSize;
    }
}