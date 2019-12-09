package com.github.jcsv.importj;

import com.github.jcsv.importj.ColValidcateProperties;

/**
 * @Auther: lipeng
 * @Date: 2019/9/1 00:24
 * @Description:
 */
@FunctionalInterface
public interface Validator {
    public boolean validcate(String value, ColValidcateProperties col, String[] row, Object params);
}
