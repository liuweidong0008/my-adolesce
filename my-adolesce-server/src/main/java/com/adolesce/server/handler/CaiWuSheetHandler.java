package com.adolesce.server.handler;

import com.adolesce.server.vo.excel.CaiWuMsgImportVo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.util.List;

/**
 * 自定义Sheet基于Sax的解析处理器
 * 处理每一行数据读取
 * 实现接口org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler
 */
@Data
@NoArgsConstructor
public class CaiWuSheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
    //封装实体对象
    private CaiWuMsgImportVo entity;
    private List<CaiWuMsgImportVo> entityList;

    public CaiWuSheetHandler(List<CaiWuMsgImportVo> entityList) {
        this.entityList = entityList;
    }

    /**
     * 解析行开始
     */
    @Override
    public void startRow(int rowNum) {
        if (rowNum > 2) {
            entity = new CaiWuMsgImportVo();
        }
    }

    /**
     * 解析每一个单元格
     * cellReference       单元格名称
     * formattedValue      单元格数据值
     * comment             单元格批注
     */
    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
        if (entity != null) {
            //因为单元格名称比较长，所以截取首字母
            switch (cellReference.substring(0, 1)) {
                case "A":
                    entity.setSeriNo(Integer.parseInt(formattedValue));
                    break;
                case "B":
                    entity.setCompanyName(formattedValue);
                    break;
                case "C":
                    entity.setCompanyNo(formattedValue);
                    break;
                case "D":
                    entity.setSystemName(formattedValue);
                    break;
                case "E":
                    entity.setSystemNo(formattedValue);
                    break;
                case "F":
                    entity.setDbName(formattedValue);
                    break;
                case "G":
                    entity.setTableEnName(formattedValue);
                    break;
                case "I":
                    entity.setFieldEnName(formattedValue);
                    break;
                case "J":
                    entity.setFieldChName(formattedValue);
                    break;
                case "K":
                    entity.setFieldBusinessDes(formattedValue);
                    break;
                case "L":
                    entity.setFieldType(formattedValue);
                    break;
                case "M":
                    entity.setIsPk(formattedValue);
                    break;
                case "N":
                    entity.setIsRequired(formattedValue);
                    break;
                case "O":
                    entity.setIsSensitiveField(formattedValue);
                    break;
                case "P":
                    entity.setIsReferData(formattedValue);
                    break;
                case "Q":
                    entity.setDataMappingRelation(formattedValue);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * A header or footer has been encountered
     *
     * @param text
     * @param isHeader
     * @param tagName
     */
    @Override
    public void headerFooter(String text, boolean isHeader, String tagName) {

    }

    /**
     * 解析每一行结束时触发
     */
    public void endRow(int rowNum) {
        //System.out.println(entity);
        //System.err.println("已经解析完第：" + rowNum + "行");
        //this.entityList.add(entity);
        //一般进行使用对象进行业务处理.....
    }
}   