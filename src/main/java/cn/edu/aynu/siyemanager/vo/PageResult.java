package cn.edu.aynu.siyemanager.vo;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;  // 当前页数据
    private long total;       // 总记录数
    private int page;         // 当前页码
    private int pageSize;     // 每页数量

    // 计算总页数
    public int getPages() {
        if (pageSize == 0) return 0;
        return (int) Math.ceil((double) total / pageSize);
    }

    // 是否还有上一页
    public boolean hasPrevious() {
        return page > 1;
    }

    // 是否还有下一页
    public boolean hasNext() {
        return page < getPages();
    }
}