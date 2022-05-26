/*******************************************************************************
 * @(#)ReadConfig.java 2019年08月29日 10:42
 * Copyright 2019 明医众禾科技（北京）有限责任公司. All rights reserved.
 *******************************************************************************/
package com.adolesce.common.utils;

import cn.hutool.core.collection.CollUtil;
import com.adolesce.common.utils.excel.export.ExcelSheet;
import com.adolesce.common.utils.excel.imports.ExcelImportConfig;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.*;

/**
 * <b>Application name：</b> ReadConfig.java <br>
 * <b>Application describing： </b> <br>
 * <b>Copyright：</b> Copyright &copy; 2019 明医众禾科技（北京）有限责任公司 版权所有。<br>
 * <b>Company：</b> 明医众禾科技（北京）有限责任公司 <br>
 * <b>Date：</b> 2019年08月29日 10:42 <br>
 * <b>@author：</b> <area href="mailto:liuWeidong@miyzh.com"> 刘威东 </area> <br>
 * <b>@version：</b>V1.0.0 <br>
 */
@Slf4j
public class XmlReadHelper {
    private static Map<String, List<ExcelSheet>> excelExportConfigMap = new HashMap<>();
    private static Map<String, ExcelImportConfig> excelImportConfigMap = new HashMap<>();

    static {
        try {
            //解析Excel导出配置文件
            ClassPathResource configFile = new ClassPathResource("config/xml/excelExportConfig.xml");
            paseExcelExportConfig(configFile.getFile());
            //解析Excel导入配置文件
            configFile = new ClassPathResource("config/xml/excelImportConfig.xml");
            paseExcelImportConfig(configFile.getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析Excel导出配置
     *
     * @param configFile excel导出配置文件
     * @throws Exception
     */
    private static void paseExcelExportConfig(File configFile) throws Exception {
        if (Objects.isNull(configFile)) {
            return;
        }
        SAXReader reader = new SAXReader();
        Document document = reader.read(configFile);
        Element root = document.getRootElement();
        List<Element> configs = root.elements("config");
        if (CollUtil.isEmpty(configs)) {
            return;
        }
        for (Element config : configs) {
            String configName = config.attributeValue("name");
            if (StringUtils.isEmpty(configName)) {
                log.error("读取Excel导出配置文件，config缺少属性：name");
                continue;
            }
            Element sheets = config.element("sheets");
            if (Objects.isNull(sheets)) {
                log.error("读取Excel导出配置文件，config缺少子节点：sheets,config = " + configName);
                continue;
            }
            List<Element> sheetList = sheets.elements("sheet");
            if (CollUtil.isEmpty(sheetList)) {
                log.error("读取Excel导出配置文件，config：sheets缺少子节点：sheet,config = " + configName);
                continue;
            }
            List<ExcelSheet> excelSheetList = Lists.newArrayList();
            Collections.emptyList();
            ExcelSheet excelSheet;
            for (Element sheet : sheetList) {
                excelSheet = new ExcelSheet();
                String sheetName = sheet.attributeValue("name");
                if (StringUtils.isEmpty(sheetName)) {
                    log.error("读取Excel导出配置文件，config：sheets：sheet缺少属性：name,config = " + configName);
                    continue;
                }
                excelSheet.setSheetName(sheetName);
                List<Node> headers = sheet.selectNodes("headers/header");
                if (CollUtil.isEmpty(headers)) {
                    log.error("读取Excel导出配置文件，config：sheets：sheet：headers缺少子节点：header,config = " + configName);
                    continue;
                }
                for (Node header : headers) {
                    excelSheet.addHeader(((Element) header).attributeValue("name"), ((Element) header).attributeValue("key"));
                }
                excelSheetList.add(excelSheet);
            }
            excelExportConfigMap.put(configName, excelSheetList);
        }
    }

    /**
     * 解析Excel导入配置
     *
     * @param configFile excel导入配置文件
     * @throws Exception
     */
    private static void paseExcelImportConfig(File configFile) throws DocumentException {
        if (Objects.isNull(configFile)) {
            return;
        }
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(configFile);
        Element root = document.getRootElement();
        List<Element> configs = root.elements("config");
        if (CollUtil.isEmpty(configs)) {
            return;
        }
        ExcelImportConfig importConfig;
        ExcelImportConfig.Field configField;
        for (Element config : configs) {
            //设置配置名称
            String configName = config.attributeValue("name");
            if (StringUtils.isEmpty(configName)) {
                log.error("读取Excel导入配置文件，config缺少属性：name");
                continue;
            }
            importConfig = new ExcelImportConfig();
            importConfig.setConfigName(configName);
            //设置class
            String classPath = config.elementText("classPath");
            if (StringUtils.isEmpty(classPath)) {
                log.error("读取Excel导入配置文件，config缺少子节点:classPath,config = " + configName);
                continue;
            }
            try {
                Class boClazz = Class.forName(classPath);
                importConfig.setBoClazz(boClazz);
            } catch (Exception e) {
                log.error("读取Excel导入配置文件，config转换class失败，没找到相应的Class,config = " + configName);
                continue;
            }
            //设置开始行
            String startLineStr = config.elementText("startLine");
            if (StringUtils.isEmpty(startLineStr)) {
                log.error("读取Excel导入配置文件，config缺少子节点：startLine,config = " + configName);
                continue;
            }
            try {
                Integer startLine = Integer.parseInt(startLineStr.trim());
                importConfig.setStartLine(startLine);
            } catch (Exception e) {
                log.error("读取Excel导入配置文件，config：startLine必须为数字,config = " + configName);
                continue;
            }
            //设置字段集合
            Element fields = config.element("fields");
            if (Objects.isNull(fields)) {
                log.error("读取Excel导入配置文件，config缺少子节点：fields,config = " + configName);
                continue;
            }
            List<Element> fieldList = fields.elements("field");
            if (CollUtil.isEmpty(fieldList)) {
                log.error("读取Excel导入配置文件，fields缺少子节点：field,config = " + configName);
                continue;
            }
            // excel列配置信息
            for (Element field : fieldList) {
                configField = new ExcelImportConfig.Field();
                String name = field.elementText("name");
                String desc = field.elementText("desc");
                String columnIndex = field.elementText("columnIndex");
                String allowBlank = field.elementText("allowBlank");
                String regex = field.elementText("regex");
                String errorMsg = field.elementText("regexMsg");
                if (StringUtils.isEmpty(name)) {
                    log.error("读取Excel导入配置文件，field缺少name,config = " + configName);
                    continue;
                }
                if (StringUtils.isEmpty(desc)) {
                    log.error("读取Excel导入配置文件，field缺少desc,config = " + configName);
                    continue;
                }
                if (StringUtils.isEmpty(columnIndex)) {
                    log.error("读取Excel导入配置文件，field缺少columnIndex,config = " + configName);
                    continue;
                }
                if (StringUtils.isEmpty(allowBlank)) {
                    allowBlank = "true";
                }
                configField.setFieldName(name);
                configField.setFieldDesc(desc);
                configField.setColumnIndex(Integer.parseInt(columnIndex));
                if (StringUtils.isNotEmpty(regex)) {
                    configField.setRegexStr(regex);
                    configField.setErrorMsg(errorMsg);
                }
                configField.setAlowBlank("true".equalsIgnoreCase(allowBlank));
                importConfig.addField(configField);
            }
            excelImportConfigMap.put(configName, importConfig);
        }
    }

    /**
     * 通过配置名称获取导出配置
     *
     * @param configName
     * @return
     */
    public synchronized static List<ExcelSheet> getExcelExportConfigByName(String configName) {
        if (excelExportConfigMap.containsKey(configName)) {
            return excelExportConfigMap.get(configName);
        }
        return null;
    }

    /**
     * 通过配置名称获取导入配置
     *
     * @param configName
     * @return
     */
    public synchronized static ExcelImportConfig getExcelImportConfigByName(String configName) {
        if (excelImportConfigMap.containsKey(configName)) {
            return excelImportConfigMap.get(configName);
        }
        return null;
    }
}