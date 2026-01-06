package cn.edu.aynu.siyemanager.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("area")
public class Area {
    private String code;        // 区划编码
    private String name;        // 区划名称
    private String parentCode;  // 父级编码
    private Integer level;      // 层级：1-省，2-市，3-区县
}