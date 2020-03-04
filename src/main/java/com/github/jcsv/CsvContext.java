package com.github.jcsv;

import com.alibaba.fastjson.JSON;
import com.github.jcsv.exportj.CompressUtil;
import com.github.jcsv.exportj.CsvExportException;
import com.github.jcsv.exportj.CsvExportProperties;
import com.github.jcsv.exportj.Paging;
import com.github.jcsv.importj.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: lipeng
 * @Date: 2019/7/17 18:51
 * @Description:
 */
@Slf4j
public class CsvContext {
    @Autowired
    private CsvConfig csvConfig;
    private final static String enterLine = "</br>";
    private int pageSize = 1000;

    public CsvContext(CsvConfig csvConfig) {
        this.csvConfig = csvConfig;
    }

    /**
     * @param request
     * @return
     * @throws Exception
     */
    public CsvImportResponse transfer(CsvImportRequest request){
        CsvImportProperties importc = getUploadConifg(request.getId());
        MultipartFile file = request.getFile();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String type = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!"csv".equals(type)) {
            throw new CsvImportException("请上传csv模板文件！");
        }
        if (importc == null) {
            throw new CsvImportException("请在配置文件中配置csv导入信息");
        }
        if (importc.getMaxSize() != 0 && file.getSize() >= importc.getMaxSize() * 1024 * 1024) {
            throw new CsvImportException("上传文件超过最大限制:" + importc.getMaxSize() + "m");
        }
        List<String> listString=null;
        try{
            listString = FileUtils.parseCsv(file.getInputStream());
        }catch (IOException e){
            throw new CsvImportException(e.getMessage());
        }
        if (listString == null || listString.size() <= 1) {
            throw new CsvImportException("上传文件内容为空");
        }
        if (listString == null || listString.size() <= 1) {
            throw new CsvImportException("上传文件内容为空");
        }
        StringBuilder sb = new StringBuilder();
        List<ColValidcateProperties> validcates = importc.getValicate();
        String[] headers = getHeaders(listString.get(0), importc.getSeparator());
        if (!checkHeaders(headers)) {
            throw new CsvImportException("上传文件表头信息含有非法字符");
        }
        if (importc.isCheckColumnSize() && importc.getStartRow() > 0 && validcates.size() != headers.length) {
            throw new CsvImportException("上传文件与模板不一致，请重新上传");
        }
        List<Integer> errorLineNum=new ArrayList<>();
        for (int j = importc.getStartRow() - 1; j < listString.size(); j++) {
            String line = listString.get(j);
            boolean bFlag=true;
            //错误数超过20，则只取20
            if (!request.isErrorFilter() && errorLineNum.size() >= 20) {
                break;
            }
            if (StringUtils.trim(line).length() == 0) {
                sb.append("第" + j + "行为空").append(enterLine);
                errorLineNum.add(j);
                bFlag=false;
                continue;
            }
            String[] colValues = line.split(importc.getSeparator());
            if (colValues == null || colValues.length == 0) {
                sb.append("第" + j + "行为空").append(enterLine);
                errorLineNum.add(j);
                bFlag=false;
                continue;
            }
            Map<String, Object> rs = new HashMap<String, Object>();
            if(validcates.size()>colValues.length){
                sb.append("第" + j + "行只有"+colValues.length+"列，不符合要求").append(enterLine);
                errorLineNum.add(j);
                bFlag=false;
                continue;
            }
            for (int i = 0; i < colValues.length; i++) {
                if (validcates.size() > i && validcates != null && validcates.get(i) != null) {
                    String errorRow = "第" + (j + 1) + "行";
                    if (importc.isCheckColumnSize() && validcates.size() != colValues.length) {
                        sb.append(errorRow + "包含关键字空格或者逗号").append(enterLine);
                        bFlag=false;
                        break;
                    }
                    ColValidcateProperties v = validcates.get(i);
                    if (v.isRequired() && StringUtils.isBlank(colValues[i])) {
                        sb.append(errorRow + v.getName() + "不能为空").append(enterLine);
                        bFlag=false;
                        continue;
                    }
                    if (StringUtils.isNotBlank(v.getValidateRegex()) && !colValues[i].trim().matches(v.getValidateRegex())) {
                        sb.append(errorRow + v.getName() + v.getHint()).append(enterLine);
                        bFlag=false;
                        continue;
                    }
                    if (StringUtils.isNotBlank(v.getValidator())) {
                        Validator validator = (Validator) SpringContext.getSingleton().getBean(v.getValidator());
                        if (validator != null && !validator.validcate(colValues[i].trim(), v, colValues, request.getParams())) {
                            sb.append(errorRow + v.getName() + v.getHint()).append(enterLine);
                            bFlag=false;
                            continue;
                        }
                    }
                    if (sb.length() == 0) {
                        rs.put(v.getName(), colValues[i]);
                    }
                } else {
                    if (headers.length != colValues.length) {
                        sb.append("第" + j + "行有关键字空格或者逗号").append(enterLine);
                        bFlag=false;
                        break;
                    }
                    rs.put(headers[i], colValues[i]);
                }
            }
            if(bFlag){
                result.add(rs);
            }else{
                errorLineNum.add(j);
            }
        }
        if (!request.isErrorFilter() && sb.length() > 0) {
            throw new CsvImportException(sb.toString());
        }
        if(result.size()==0){
            throw new CsvImportException("上传文件为空或者符合要求的记录数为0");
        }
        return new CsvImportResponse.Builder().setErrorCount(errorLineNum.size()).setErrorMsg(sb.toString()).setList(result).
                setTotalCount(listString.size() - importc.getStartRow()).setErrorLineNum(errorLineNum).build();
    }

    private String[] getHeaders(String headerstr, String split) {
        return headerstr.split(split);
    }


    /**
     * 表头内容校验
     *
     * @param headerStrs
     * @return
     */
    private boolean checkHeaders(String[] headerStrs) {
        Pattern pt = Pattern.compile("^[0-9a-zA-Z_]+$");
        if (headerStrs != null && headerStrs.length > 0) {
            for (String header : headerStrs) {
                header = FileUtils.specialUnicode(header);
                if (StringUtils.isNotBlank(header)) {
                    Matcher mt = pt.matcher(header);
                    if (!mt.matches()) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }


    /**
     * getUploadConifg
     *
     * @param id
     * @return
     */
    public CsvImportProperties getUploadConifg(String id) {
        for (CsvImportProperties item : csvConfig.getImportc()) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    /**
     * getExportConifg
     *
     * @param id
     * @return
     */
    public CsvExportProperties getExportConifg(String id) {
        for (CsvExportProperties item : csvConfig.getExportc()) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    /**
     * @param id
     * @param data
     * @return
     * @throws Exception
     */
    public String export(String id, List<Map> data) throws Exception {
        CsvExportProperties properties = getExportConifg(id);
        if (StringUtils.isBlank(properties.getHeaders()) || StringUtils.isBlank(properties.getCols())) {
            throw new CsvExportException("headers or cols is empty");
        }
        if (data == null || data.size() == 0) {
            throw new CsvExportException("export data is empty");
        }
        String[] cols = properties.getCols().split(",");
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> item : data) {
            List<String> mm = new ArrayList<>();
            for (String col : cols) {
                mm.add(StringUtils.defaultString((String) item.get(col), ""));
            }
            sb.append(String.join(",", mm)).append("\\r\\n");
        }
        String filePath = getTempPath();
        String result = filePath + properties.getFileName() + ".csv";
        FileUtils.createCsv(result, sb.toString());
        return result;
    }

    /**
     * @param id
     * @param page
     * @return
     * @throws Exception
     */
    public String export(String id, Paging page) throws Exception {
        return export(id, page, "");
    }

    /**
     * @param id
     * @param paging
     * @param response
     * @throws Exception
     */
    public void export(String id, Paging paging, HttpServletResponse response) throws Exception {
        String finalName = this.export(id, paging);
        FileUtils.downloadFile(response, finalName);
        org.apache.commons.io.FileUtils.deleteQuietly(new File(finalName));
    }

    /**
     * @param id
     * @param paging
     * @param prefix
     * @param response
     * @throws Exception
     */
    public void export(String id, Paging paging, String prefix, HttpServletResponse response) throws Exception {
        String finalName = this.export(id, paging, prefix);
        FileUtils.downloadFile(response, finalName);
        org.apache.commons.io.FileUtils.deleteQuietly(new File(finalName));
    }

    public void export(String id, List<Map> data, HttpServletResponse response) throws Exception {
        String finalName = this.export(id, data);
        FileUtils.downloadFile(response, finalName);
        org.apache.commons.io.FileUtils.deleteQuietly(new File(finalName));
    }

    /**
     * @param id
     * @param page
     * @param prefix
     * @return
     * @throws Exception
     */
    public String export(String id, Paging page, String prefix) throws Exception {
        CsvExportProperties properties = getExportConifg(id);
        if (StringUtils.isBlank(properties.getHeaders()) || StringUtils.isBlank(properties.getCols())) {
            throw new CsvExportException("headers or cols is empty");
        }
        if (page == null) {
            throw new CsvExportException("page is null");
        }
        String[] cols = properties.getCols().split(",");
        int pageNum = 1;
        String filePath = getTempPath();
        File fileP = new File(filePath);
        if (!fileP.exists()) {
            fileP.mkdirs();
        }
        long total = page.getTotal();
        if (total <= 0) {
            String errorFilePath = filePath + "error.csv";
            if (!new File(errorFilePath).exists()) {
                FileUtils.createCsv(errorFilePath, "导出数据为空");
            }
            return errorFilePath;
        }

        if (properties.getCompress().isEnabled()) {
            String compressPath = filePath + System.currentTimeMillis() + File.separator;
            File filec = new File(compressPath);
            if (!filec.exists()) {
                filec.mkdirs();
            }
            int fileNum = 1;
            List<Map> list = page.getList(pageSize, pageNum);
            FileUtils.createCsv(compressPath + fileNum + ".csv", properties.getHeaders() + "\r\n");
            long compressFileSize = 0;
            while (list != null && list.size() > 0) {
                log.info("查询第{}页，result size:{}", pageNum, pageSize);
                StringBuilder sb = new StringBuilder();
                for (Map<String, Object> item : list) {
                    List<String> mm = new ArrayList<>();
                    for (String col : cols) {
                        mm.add(StringUtils.defaultString((String) item.get(col), ""));
                    }
                    sb.append(String.join(",", mm)).append("\r\n");
                    compressFileSize++;
                    if (compressFileSize == properties.getCompress().getFileSize()) {
                        FileUtils.createCsv(compressPath + fileNum + ".csv", sb.toString());
                        sb.delete(0, sb.length());
                        compressFileSize = 0;
                        fileNum++;
                        FileUtils.createCsv(compressPath + fileNum + ".csv", properties.getHeaders() + "\r\n");
                    }
                }
                if (sb.length() > 0) {
                    FileUtils.createCsv(compressPath + fileNum + ".csv", sb.toString() + "\r\n");
                }
                pageNum++;
                list = page.getList(pageSize, pageNum);
            }
            String format = properties.getCompress().getType().name();
            String finalPath = filePath + prefix + properties.getFileName() + "." + format;
            CompressUtil.generateFile(compressPath, format, finalPath);
            org.apache.commons.io.FileUtils.deleteDirectory(fileP);
            return finalPath;
        } else {
            if (total > 500000) {
                log.error("properties :{}", JSON.toJSONString(properties));
                throw new CsvExportException("数据量超过50W，建议使用压缩导出模式，请修改导出配置");
            }
            List<Map> list = page.getList(pageSize, pageNum);

            String finalPath = filePath + properties.getFileName() + System.currentTimeMillis() + ".csv";
            FileUtils.createCsv(finalPath, properties.getHeaders() + "\r\n");
            while (list != null && list.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (Map<String, Object> item : list) {
                    List<String> mm = new ArrayList<>();
                    for (String col : cols) {
                        mm.add(StringUtils.defaultString((String) item.get(col), ""));
                    }
                    sb.append(String.join(",", mm)).append("\r\n");
                }
                FileUtils.createCsv(finalPath, sb.toString());
                pageNum++;
                list = page.getList(pageSize, pageNum);
            }
            return finalPath;
        }

    }

    private String getTempPath() throws IOException {
        return csvConfig.getTempFilePath() + File.separator
                + DateFormatUtils.format(new Date(), "yyyyMMdd") + File.separator;
    }


}
