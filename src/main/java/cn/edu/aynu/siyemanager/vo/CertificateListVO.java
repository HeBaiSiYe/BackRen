package cn.edu.aynu.siyemanager.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CertificateListVO {
    private Long id;
    private Long personId;
    private String personName;
    private Integer type;
    private String number;
    private LocalDate issueDate;
    private LocalDate expireDate;
    private Integer status;
    private LocalDateTime createTime;
}