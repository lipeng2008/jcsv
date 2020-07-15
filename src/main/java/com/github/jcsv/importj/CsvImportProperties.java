package com.github.jcsv.importj;

import com.github.jcsv.importj.ColValidcateProperties;
import lombok.Data;

import java.util.List;

/**
 * @Auther: lipeng
 * @Date: 2019/7/17 17:57
 * @Description:
 */
@Data
public class CsvImportProperties {
    private String id;
    private int maxSize;
    private int startRow;
    private List<ColValidcateProperties> valicate;
    private String headerValidcate;
    private String desc;
    private String separator;
    private boolean checkColumnSize=true;
    private boolean colFromHeader=false;

}
