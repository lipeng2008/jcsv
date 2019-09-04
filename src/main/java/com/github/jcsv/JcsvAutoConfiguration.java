package com.github.jcsv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: lipeng
 * @Date: 2019/8/31 15:18
 * @Description:
 */
@Configuration
@EnableConfigurationProperties(CsvConfig.class)
@ConditionalOnProperty(name = "csv-config",matchIfMissing = true)
@Slf4j
public class JcsvAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CsvConfig csvConfig() {
        log.info("init csvConfig");
        return new CsvConfig();
    }
    @Bean
    @ConditionalOnMissingBean
    public CsvContext csvContext() {
        log.info("init csvContext");
        return new CsvContext(csvConfig());
    }

    /**
     * @return Enregistrement du context Spring.
     */
    @Bean
    public SpringContext springContext() {
        return new SpringContext();
    }
}
