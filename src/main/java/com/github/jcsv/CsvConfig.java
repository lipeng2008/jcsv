package com.github.jcsv;

import com.github.jcsv.exportj.CsvExportProperties;
import com.github.jcsv.importj.CsvImportProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: lipeng
 * @Date: 2019/7/17 18:11
 * @Description:
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "csv-config")
@Data
public class CsvConfig {
    private List<CsvExportProperties> exportc;
    private List<CsvImportProperties> importc;
    private String tempFilePath;

}
