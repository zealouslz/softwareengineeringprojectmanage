package com.zeal.softwareengineeringprojectmanage.service;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.util.List;

public interface ImportService {
    public  List<List<Object>> getBankListByExcel(InputStream in, String fileName) throws Exception;
    public Workbook getWorkbook(InputStream inStr, String fileName) throws Exception;
}
