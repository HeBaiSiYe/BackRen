package cn.edu.aynu.siyemanager.dto;

import lombok.Data;

@Data
public class CertificateAddDTO {
    private Long personId;
    private Integer type;
    private String number;
    private Integer validYears;
}