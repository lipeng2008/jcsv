package com.github.jcsv.importj;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @Auther: lipeng
 * @Date: 2020/2/17 15:59
 * @Description:
 */
@Data
public class CsvImportRequest {
    private MultipartFile uploadFile;
    private File localFile;
    private String id;
    private Object params;
    private boolean errorFilter=false;

    public static class Builder {
        private MultipartFile uploadFile;
        private File localFile;
        private String id;
        private Object params;
        private boolean errorFilter=false;

        public Builder setUploadFile(MultipartFile uploadFile) {
            this.uploadFile = uploadFile;
            return this;
        }
        public Builder setLocalFile(File localFile) {
            this.localFile = localFile;
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
        this.uploadFile=builder.uploadFile;
        this.localFile=builder.localFile;
        this.errorFilter=builder.errorFilter;
        this.id=builder.id;
        this.params=builder.params;

    }

    public String getFileName(){
        if(localFile==null&&uploadFile==null){
            throw new CsvImportException("请指定导入的csv文件");
        }
        if(localFile!=null){
            return localFile.getName();
        }
        if(uploadFile!=null){
            return uploadFile.getOriginalFilename();
        }
        return null;
    }

    public long getFileSize(){
        if(localFile==null&&uploadFile==null){
            throw new CsvImportException("请指定导入的csv文件");
        }
        if(localFile!=null){
            return localFile.length();
        }
        if(uploadFile!=null){
            return uploadFile.getSize();
        }
        return 0;
    }

    public InputStream getInputStream() throws FileNotFoundException , IOException {
        if(localFile==null&&uploadFile==null){
            throw new CsvImportException("请指定导入的csv文件");
        }
        if(localFile!=null&&localFile.exists()){
            return new FileInputStream(localFile);
        }
        if(uploadFile!=null){
            return uploadFile.getInputStream();
        }
        return null;
    }



}
