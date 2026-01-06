package cn.edu.aynu.siyemanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PersonSearchDTO {

    @NotBlank(message = "姓名不能为空")
    private String name;        // 姓名

    private String idCard;      // 身份证号
}