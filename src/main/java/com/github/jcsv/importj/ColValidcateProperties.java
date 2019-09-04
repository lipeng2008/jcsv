package com.github.jcsv.importj;

import lombok.Data;

/**
 * @Auther: lipeng
 * @Date: 2019/7/17 17:58
 * @Description:
 */
@Data
public class ColValidcateProperties {
    private int col;

    private String name;

    private boolean required;

    private String validateRegex;

    private String hint;

    private String validator;

}
