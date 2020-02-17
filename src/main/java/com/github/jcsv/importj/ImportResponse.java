package com.github.jcsv.importj;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Auther: lipeng
 * @Date: 2020/2/17 15:36
 * @Description:
 */
@Data
public class ImportResponse {
    private List<Map<String,Object>> list;
    private String errorMsg;
    private int totalCount;
    private int errorCount;

    public static class Builder {
        private List<Map<String,Object>> list;
        private String errorMsg;
        private int totalCount;
        private int errorCount;

        public Builder setList(List<Map<String, Object>> list) {
            this.list = list;
            return this;
        }

        public Builder setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return this;
        }

        public Builder setTotalCount(int totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public Builder setErrorCount(int errorCount) {
            this.errorCount = errorCount;
            return this;
        }

        public ImportResponse build() {
            return new ImportResponse(this);
        }
    }
    private ImportResponse(ImportResponse.Builder builder){
        this.list=builder.list;
        this.errorCount=builder.errorCount;
        this.totalCount=builder.totalCount;
        this.errorMsg=builder.errorMsg;
    }
}
