package com.github.jcsv.exportj;

import java.util.List;
import java.util.Map;

/**
 * @Auther: lipeng
 * @Date: 2019/8/29 09:38
 * @Description:
 */
public interface Paging {
    public List<Map> getList(long pageSize, int pageNum);

    public long getTotal();
}
