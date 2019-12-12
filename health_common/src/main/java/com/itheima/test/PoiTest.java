package com.itheima.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 使用POI读取Excel文件
 */
public class PoiTest {

    @Test
    public void test1() throws Exception {
        //加载指定文件,创建一个Excel对象(工作簿)
        XSSFWorkbook excel= new XSSFWorkbook(new FileInputStream(new File("d:\\新建 XLSX 工作表.xlsx")));
        //读取Excel文件中第一个Sheet标签页
        XSSFSheet sheet = excel.getSheetAt(0);
        //遍历Sheet标签页,获得每一行数据
        for (Row row : sheet) {
            //遍历行,获取每个单元个对象
            for (Cell cell : row) {
                System.out.println(cell.getStringCellValue());
            }
        }
        //关闭资源
        excel.close();
    }

    @Test
    public void test2() throws Exception {
        //加载指定文件,创建一个Excel对象(工作簿)
        XSSFWorkbook excel= new XSSFWorkbook(new FileInputStream(new File("d:\\新建 XLSX 工作表.xlsx")));
        //读取Excel文件中第一个Sheet标签页
        XSSFSheet sheet = excel.getSheetAt(0);
        //获得当前工作表中最后一个行号,需要注意:行号从0开始
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 0; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);//根据行号获取每一行
            //获得当前行最后一个单元索引
            short lastCellNum = row.getLastCellNum();
            for (int j = 0; j < lastCellNum; j++) {
                XSSFCell cell = row.getCell(j);//根据单元格获得单元格对象
                System.out.println(cell.getStringCellValue());
            }
        }
        //关闭资源
        excel.close();
    }

    @Test
    public void test3()throws Exception{
        //在内存中创建一个Excel文件(工作簿)
        XSSFWorkbook excel= new XSSFWorkbook();
        //创建一个工作表
        XSSFSheet sheet = excel.createSheet("花名册");
        //在工作表中创建行对象
        XSSFRow title = sheet.createRow(0);
        //在行中创建单元格对象
        title.createCell(0).setCellValue("姓名");
        title.createCell(1).setCellValue("年龄");
        title.createCell(2).setCellValue("性别");

        XSSFRow data = sheet.createRow(0);
        //在行中创建单元格对象
        data.createCell(0).setCellValue("阿强");
        data.createCell(1).setCellValue("21");
        data.createCell(2).setCellValue("男");


        //创建一个输出流,通过输出流将内存中的Excel文件写到磁盘
        FileOutputStream out = new FileOutputStream(new File("d:\\hello.xlsx"));
        excel.write(out);
        out.flush();
        excel.close();
    }
}
