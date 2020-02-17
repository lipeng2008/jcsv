package com.github.jcsv.importj;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Auther: lipeng
 * @Date: 2020/2/17 15:36
 * @Description:
 */
@Data
public class CsvImportResponse {
    private List<Map<String,Object>> list;
    private String errorMsg;
    private int totalCount;
    private int errorCount;
    private List<Integer> errorLineNum;

    public static class Builder {
        private List<Map<String,Object>> list;
        private String errorMsg;
        private int totalCount;
        private int errorCount;
        private List<Integer> errorLineNum;

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

        public Builder setErrorLineNum(List<Integer> errorLineNum) {
            this.errorLineNum = errorLineNum;
            return this;
        }

        public CsvImportResponse build() {
            return new CsvImportResponse(this);
        }
    }
    private CsvImportResponse(CsvImportResponse.Builder builder){
        this.list=builder.list;
        this.errorCount=builder.errorCount;
        this.totalCount=builder.totalCount;
        this.errorMsg=builder.errorMsg;
        this.errorLineNum=builder.errorLineNum;
    }
}
