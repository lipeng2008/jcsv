package com.github.jcsv.importj;

import java.util.List;

/**
 * @Auther: lipeng
 * @Date: 2020/7/15 15:00
 * @Description:
 */
public interface HeaderValidator {
    public boolean validcate(String[] headerStrs, List<ColValidcateProperties> valicates);
}
