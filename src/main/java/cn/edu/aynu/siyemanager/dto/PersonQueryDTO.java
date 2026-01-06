package cn.edu.aynu.siyemanager.dto;

import lombok.Data;

@Data
public class PersonQueryDTO {
    // 分页参数
    private Integer page = 1;
    private Integer pageSize = 10;

    // 查询条件
    private String name;           // 姓名
    private String idCard;         // 身份证号
    private Integer personType;    // 人口类型：1-户籍地人口，2-流动人口，3-重点人口
    private Integer personStatus;  // 人员状态：1-正常，2-死亡
    private Long householdId;      // 户籍ID

    /**
     * 计算偏移量
     */
    public Integer getOffset() {
        if (page == null || pageSize == null) {
            return 0;
        }
        return (page - 1) * pageSize;
    }
}