package com.github.jcsv.exportj;

import lombok.Data;

/**
 * @Auther: lipeng
 * @Date: 2019/8/18 21:28
 * @Description:
 */
@Data
public class CsvExportProperties {
    private String id;
    private String headers;
    private String desc;
    private String cols;
    private String fileName;
    private FileCompressProperties compress;
}