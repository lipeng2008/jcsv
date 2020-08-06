package com.github.jcsv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lipeng
 * @Description: fileUtils
 * @Date: 2018-12-17 13:35
 */
@Slf4j
public class FileUtils {

    /**
     * uploadFile
     * @param file
     * @param filePath
     * @param fileName
     * @return
     */
    public static boolean uploadFile(MultipartFile file, String filePath, String fileName) {
        // 生成新的文件名
        //String realPath = path + "/" + FileNameUtils.getFileName(fileName);
        //使用原文件名
        String realPath = filePath + File.separator+ fileName;
        File dest = new File(realPath);
        //判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            //保存文件
            file.transferTo(dest);
        } catch (IllegalStateException e) {
            return false;
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            return false;
        }
        return true;
    }


    /**
     * read csv
     * @param in
     * @return
     * @throws FileNotFoundException
     */
    public static List<String> parseCsv(InputStream in) throws FileNotFoundException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in));
            String line = "";
            String everyLine = "";
            List<String> allString = new ArrayList<>();
            //读取到的内容给line变量
            while ((line = br.readLine()) != null) {
                everyLine = line;
              //  log.info("everyLine: {}, everyLine length: {}", everyLine, everyLine.length());
                //if (everyLine.length() > 0) {
                 allString.add(everyLine);
                //}
            }
            return allString;
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.warn("br close error: ",e);
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * read csv
     * @param csv
     * @return
     * @throws FileNotFoundException
     */
    public static List<String> parseCsv(File csv) throws FileNotFoundException {
        return parseCsv(new FileInputStream(csv));
    }


    /**
     * 解析csv文件并返回总条数
     *
     * @throws Exception
     */
    public static List<String> parseCsv(String filePath) throws Exception {
        return parseCsv(new File(filePath));
    }


    /**
     *  create csv
     * @param destFile
     * @param str
     * @return line
     * @throws Exception
     */
    public static void createCsv(String destFile,String str) throws Exception {
        File file=new File(destFile);
        if(file.getParentFile()!=null&&!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(destFile,true);
            fos.write(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF });
            fos.write(str.getBytes("UTF-8"));
            fos.close();
        }catch (Exception e){
            log.error("createCsv error:{}",e.getMessage());
        }finally {
            if(fos!=null){
                fos.close();
            }
        }

    }


    /**
     *  append csv
     * @param destFile
     * @param sourceFile
     * @return line
     * @throws Exception
     */
    public static int appendParseCsv(String destFile,String sourceFile,int start) throws Exception {
        BufferedWriter bw=null;
        BufferedReader br = null;
        try{
            File dest=new File(destFile);
            if(!dest.exists()){
                dest.createNewFile();
            }
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest, true), "UTF-8"), 1024);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile),"UTF-8"));
            String line ;
            int i=1;
            int lineCount=0;
            //读取到的内容给line变量
            while ((line = br.readLine()) != null) {
                //  log.info("everyLine: {}, everyLine length: {}", everyLine, everyLine.length());
                if (line.length() > 0&&i>=start) {
                    lineCount++;
                    bw.write(line);
                    bw.newLine();
                }
                i++;
            }
            bw.flush();
            return lineCount;
        }catch (Exception e){
            log.error("appendParseCsv error:{}",e.getMessage());
            return 0;
        }finally {
            if(bw!=null){
                bw.close();
            }
            if(br!=null){
                br.close();
            }
        }

    }


    /**
     * downloadFile
     *
     * @param response
     * @param filePath
     * @throws ServletException
     * @throws IOException
     */
    public static void downloadFile(HttpServletResponse response, String filePath) throws ServletException, IOException {

        File filename = new File(filePath);
        // 设置文件ContentType类型，这样设置，会自动判断下载文件类型  
        response.setContentType("multipart/form-data");
        // 设置编码格式
        response.setCharacterEncoding("UTF-8");
        // 设置可以识别Html文件  
        response.setContentType("text/html");
        // 2.设置文件头：最后一个参数是设置下载文件名  
        response.setHeader("Content-Disposition", "attachment;filename=" + filename.getName());
        //System.out.println("fileName = " + filename.getName());
        // （这里  可以设置成excel格式 ：response.setHeader("Content-Disposition",
        // "attachment;fileName=" + “文件名” + ".xsl");
        // 可以设置成.pdf格式 ：response.setHeader("Content-Disposition",
        // "attachment;fileName=" + “文件名” + ".pdf");
        OutputStream out = response.getOutputStream();
        FileInputStream fileinput = new FileInputStream(filename);
        try {
            out = response.getOutputStream();
            int b = 0;
            byte[] buffer = new byte[1024];
            while ((b = fileinput.read(buffer)) != -1) {
                // 4.写到输出流(out)中  
                out.write(buffer, 0, b);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            fileinput.close();
            out.flush();
            out.close();
        }
    }

    /**
     * delete [ "\uFEFF" ]
     *
     * @param str
     * @return
     */
    public static String specialUnicode(String str){
        if (str.startsWith("\uFEFF")){
            str = str.replace("\uFEFF", "");
        }else if (str.endsWith("\uFEFF")){
            str = str.replace("\uFEFF","");
        }
        return str;
    }

}
