package com.zksite.common.utils.poi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zksite.common.utils.Reflections;
import com.zksite.common.utils.poi.annotaion.ExcelField;

public class ExcelUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

    private static Workbook createXSSFWorkbook() {
        return new XSSFWorkbook();
    }

    private static void addTitle(String title, Workbook workbook, int sheetAt) {
        Sheet sheet = workbook.getSheetAt(sheetAt);
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        sheet.createRow(2);
        CellRangeAddress cra = new CellRangeAddress(0, 2, 0, 20);
        sheet.addMergedRegion(cra);
        Font font = workbook.createFont();
        font.setFontName("微软雅黑");
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 20);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);// 设置水平居中
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 设置垂直居中
        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(title);
    }

    private static void addCellTitle(List<String> list, Workbook workbook, int sheetAt) {
        Sheet sheet = workbook.getSheetAt(sheetAt);
        int lastRowNum = sheet.getLastRowNum();
        Row row = sheet.createRow(lastRowNum + 1);
        int index = 0;
        Font font = workbook.createFont();
        font.setFontName("微软雅黑");
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);// 设置水平居中
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 设置垂直居中
        cellStyle.setFont(font);
        for (String title : list) {
            Cell cell = row.createCell(index);
            cell.setCellValue(title);
            cell.setCellStyle(cellStyle);
            index++;
        }
    }

    /**
     * 导出带标题和指定字段excel<br>
     * 如果clazz
     * 里的字段带有{@link com.zksite.common.utils.poi.annotaion.ExcelField}注解和导出指定属性的字段没有被注解，将不会被导出，否则将导出指定属性的数据
     * 
     * @param list 数据列表
     * @param clazz 数据类型
     * @param filedNames 导出指定属性名称
     * @param tiltle 标题
     */
    public static <T> Workbook exportXSSFWorkbook(List<T> list, Class<T> clazz, String tiltle,
            List<String> filedNames) {
        List<Field> fields = Reflections.getFileds(clazz);
        List<Field> filterFileds = filterFileds(fields, filedNames);
        Workbook workbook = createXSSFWorkbook();
        workbook.createSheet();
        addTitle(tiltle, workbook, 0);// 添加标题
        addCellTitle(getCellTitles(filterFileds), workbook, 0);// 添加列标题
        writeData(list, filterFileds, workbook, 0);// 写数据
        return workbook;
    }


    /**
     * 导出指定字段excel<br>
     * 
     * 如果clazz
     * 里的字段带有{@link com.zksite.common.utils.poi.annotaion.ExcelField}注解和导出指定属性的字段没有被注解，将不会被导出，否则将导出指定属性的数据
     * 
     * @param list 数据列表
     * @param clazz 数据类型
     * @param filedNames 指定字段列表
     */
    public static <T> Workbook exportXSSFWorkbook(List<T> list, Class<T> clazz,
            List<String> filedNames) {
        List<Field> fields = Reflections.getFileds(clazz);
        List<Field> filterFileds = filterFileds(fields, filedNames);
        Workbook workbook = createXSSFWorkbook();
        workbook.createSheet();
        addCellTitle(getCellTitles(filterFileds), workbook, 0);// 添加列标题
        writeData(list, filterFileds, workbook, 0);// 写数据
        return workbook;
    }

    /**
     * 导出excel<br>
     * 如果clazz里的字段带有{@link com.zksite.common.utils.poi.annotaion.ExcelField}注解，那么导出被注解的数据，如果没有将导出所有字段
     * 
     * @param list 数据列表
     * @param clazz 数据类型
     * @param title 标题
     */
    public static <T> Workbook exportXSSFWorkbook(List<T> list, Class<T> clazz, String title) {
        List<Field> fields = Reflections.getFileds(clazz);
        Workbook workbook = createXSSFWorkbook();
        workbook.createSheet();
        addTitle(title, workbook, 0);
        addCellTitle(getCellTitles(fields), workbook, 0);// 添加列标题
        writeData(list, fields, workbook, 0);// 写数据
        return workbook;
    }

    /**
     * 导出excel<br>
     * 如果clazz里的字段带有{@link com.zksite.common.utils.poi.annotaion.ExcelField}注解，那么导出被注解的数据，如果没有将导出所有字段
     * 
     * @param list 数据列表
     * @param clazz 数据类型
     */
    public static <T> Workbook exportXSSFWorkbook(List<T> list, Class<T> clazz) {
        List<Field> fields = Reflections.getFileds(clazz);
        Workbook workbook = createXSSFWorkbook();
        workbook.createSheet();
        addCellTitle(getCellTitles(fields), workbook, 0);// 添加列标题
        writeData(list, fields, workbook, 0);// 写数据
        return workbook;
    }

    private static List<String> getCellTitles(List<Field> fields) {
        List<String> titles = new ArrayList<String>(fields.size());
        for (Field field : fields) {
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            if (excelField != null) {
                titles.add(excelField.name());
            } else {
                titles.add(field.getName());
            }
        }
        return titles;
    }

    private static <T> void writeData(List<T> list, List<Field> fields, Workbook workbook,
            int sheetAt) {
        Sheet sheet = workbook.getSheetAt(sheetAt);
        int lastRowNum = sheet.getLastRowNum();
        int currentRow = lastRowNum + 1;
        int valueLength = 0;
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            Reflections.makeAccessible(field);
            for (T t : list) {
                try {

                    Object object = field.get(t);
                    String value = "";
                    if (object != null) {
                        if (object.getClass().isAssignableFrom(Date.class)) {
                            value = getDateValue((Date) object, field);
                        } else {
                            value = object.toString();
                        }
                    }
                    Row row = sheet.getLastRowNum() >= currentRow ? sheet.getRow(currentRow)
                            : sheet.createRow(currentRow);
                    Cell cell = row.createCell(i);
                    valueLength += value.length();
                    cell.setCellValue(value);
                    currentRow++;
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            double averageLength = (double) valueLength / list.size() + 3;
            sheet.setColumnWidth(i, (int) (averageLength * 250));// 修正宽度
            currentRow = lastRowNum + 1;
            valueLength = 0;
        }
    }

    private static String getDateValue(Date date, Field field) {
        ExcelField excelField = field.getAnnotation(ExcelField.class);
        String format = null;
        if (excelField != null && StringUtils.isNotBlank(excelField.dateFormat())) {
            format = excelField.dateFormat();
        } else {
            if (date.getTime() % 1000 == 0) {
                format = "yyyy-MM-dd";
            } else {
                format = "yyyy-MM-dd HH:mm:ss";
            }
        }
        return DateFormatUtils.format(date, format);
    }

    private static List<Field> filterFileds(List<Field> fileds, List<String> filedNames) {
        List<Field> newList = new ArrayList<Field>(filedNames.size());
        for (String name : filedNames) {
            for (Field field : fileds) {
                if (field.getName().equals(name)) {
                    newList.add(field);
                    continue;
                }
            }
        }
        return newList;
    }

}
