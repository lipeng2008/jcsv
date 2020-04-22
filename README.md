# jcsv

#### 介绍

jcsv一个简单的、轻量级的csv导入、导出库，相对于opencsv与javacsv，jcsv侧重于导入导出，包括导入校验、导出模板等。

#### 使用说明

jcsv现在只支持集成到springboot工程中

#### 依赖库

```
<dependency>
  <groupId>com.github.lipengxs.jcsv</groupId>
  <artifactId>jcsv-spring-boot-starter</artifactId>
  <version>1.0.2</version>
</dependency>
```



#### 配置说明

```yaml
csv-config:
    temp-file: D:/temp
    exportc:
      - id: tag_val_detail_export
        file-name: 商品明细
        compress:
          enabled: true
          file-size: 100000
          type: zip
        headers: 商品ID,xxxx,商品名称,xxx,xxx,xxxx
        cols: goods_id,goods_sn,goods_name,site_tp,site_id,new_cate_1_nm
        desc: "导出商品明细"
  importc:
    - id: throng-0
      desc : "上传member_id"
      max-size: 30 #单位m
      start-row: 2
      separator: ","
      valicate:
        - { col: 0, name: member_id,validateRegex: "^\\d{1,10}$", hint: "请填写10位以内的数字",required: true}
    - id: throng-1
      desc : "上传email+语言+站点"
      max-size: 30 #单位m
      separator: ","
      start-row: 2
      valicate:
        - { col: 0, name: email,required: true,validateRegex: "^[éûàçùôîèíá¡N¿UóñúäöüßàèùåäöãáéàêçíşçöüğşàâäèéêëîïôœùûüÿçÀÂÄÈÉÊËÎÏÔŒÙÛÜŸÇA-Za-z0-9_\\-\\.\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", hint: "邮箱地址错误"}
        - { col: 1, name: language ,required: true,hint: "语言错误" ,td-id: 11}
        - { col: 2, name: site_id,required: true,hint: "站点错误" ,td-id: 13 }
    - id: throng-3
      desc: "上传member_id+占位符"
      max-size: 30 #单位m
      separator: ","
      start-row: 2
      check-column-size: false //该字段是表示不限制字段个数，没有在valicate中配置不需要校验
      valicate:
        - { col: 0, name: member_id,validateRegex: "^\\d{1,10}$", hint: "请填写10位以内的数字",required: true}
    - id: throng-4
      desc: "上传email"
      max-size: 30 #单位m
      start-row: 2
      separator: ","
      valicate:
        - { col: 0, name: email,required: true,validateRegex: "^[éûàçùôîèíá¡N¿UóñúäöüßàèùåäöãáéàêçíşçöüğşàâäèéêëîïôœùûüÿçÀÂÄÈÉÊËÎÏÔŒÙÛÜŸÇA-Za-z0-9_\\-\\.\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", hint: "邮箱地址错误"}

```



#### 导入

```
@Autowize
private CsvContext csvContext;
public BaseDataAPI upload(HttpServletRequest request,
                                        @RequestParam("file") MultipartFile file){
try {
            if(file==null){
                return D.error("请上传文件");
            }
            CsvImportRequest ir=new CsvImportRequest.Builder().setErrorFilter(filterError).setFile(file)
                               .setId("upload-1").build();
             //解析模板内容，对比模板内容是否和上传的一样
            CsvImportResponse response= csvContext.transfer(ir);
            return D.ok(response.getList());
        } catch (CsvImportException e) {
            return D.error(e.getMessage());
        } catch (Exception e) {
            return D.error(e.getMessage());
        }
 }
```



#### 导出
##### 压缩分页导出
```
@Autowize
private CsvContext csvContext;
public BaseDataAPI export(){
    try{
            String filePath=csvContext.export("tag_val_detail_export", new Paging(){
                private String cursor;
                @Override
                public List<Map> getList(long pageSize, int pageNum) {
                    return service.getPageList(params,pageSize,pageNum);
                }

                public long getTotal(){
                    return service.getTotal(params);
                }
            });
            FileUtils.downloadFile(response,filePath);
            org.apache.commons.io.FileUtils.deleteQuietly(new File(filePath));
            return null;
        }catch (Exception e){
            logger.error("exportGoodsDetail error",e);
            return D.error(e.getMessage());
        }
 }
```

```
csv-config:
  temp-file: D:/temp
  exportc:
    - id: tag_val_detail_export
      file-name: 商品明细
      compress:
        enabled: true  //这里必须开启，如果为false则只会导出到一个文件，且不会压缩
        file-size: 100000
        type: zip //支持zip、rar
```

##### 根据查询结果导出
```
@Autowize
private CsvContext csvContext;
public BaseDataAPI export(){
    try{
            List<Map> data=service.getList();
            String filePath=csvContext.export("tag_val_detail_export", data);
            FileUtils.downloadFile(response,filePath);
            org.apache.commons.io.FileUtils.deleteQuietly(new File(filePath));
            return null;
        }catch (Exception e){
            logger.error("exportGoodsDetail error",e);
            return D.error(e.getMessage());
        }
 }
```
