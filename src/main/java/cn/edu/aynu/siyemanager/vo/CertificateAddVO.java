package cn.edu.aynu.siyemanager.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CertificateAddVO {
    private Long id;
    private Long personId;
    private Integer type;
    private String typeName;
    private String number;
    private LocalDate issueDate;
    private LocalDate expireDate;
    private Integer status;
    private String statusName;
    private LocalDateTime createTime;
}