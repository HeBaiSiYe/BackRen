package cn.edu.aynu.siyemanager.dto;

import lombok.Data;

@Data
public class CertificateQueryDTO {
    private Integer page;
    private Integer pageSize;
    private Integer type;
    private Integer status;
    private String keyword;
    private Long personId;

    // 分页计算字段
    private Integer offset;

    // 设置offset的方法
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}