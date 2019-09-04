package com.github.jcsv.exportj;

import lombok.Data;

/**
 * @Auther: lipeng
 * @Date: 2019/8/18 21:31
 * @Description:
 */
@Data
public class FileCompressProperties {
    private boolean enabled=false;
    private CompressType type;
    private long fileSize;
    public enum CompressType{
        zip,rar;
    }
}
