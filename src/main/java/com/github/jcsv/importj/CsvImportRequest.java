package com.github.jcsv.importj;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: lipeng
 * @Date: 2020/2/17 15:59
 * @Description:
 */
@Data
public class CsvImportRequest {
    private MultipartFile file;
    private String id;
    private Object params;
    private boolean errorFilter=false;

    public static class Builder {
        private MultipartFile file;
        private String id;
        private Object params;
        private boolean errorFilter=false;

        public Builder setFile(MultipartFile file) {
            this.file = file;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setParams(Object params) {
            this.params = params;
            return this;
        }

        public Builder setErrorFilter(boolean errorFilter) {
            this.errorFilter = errorFilter;
            return this;
        }

        public CsvImportRequest build() {
            return new CsvImportRequest(this);
        }
    }
    private CsvImportRequest(Builder builder){
        this.file=builder.file;
        this.errorFilter=builder.errorFilter;
        this.id=builder.id;
        this.params=builder.params;
    }



}
