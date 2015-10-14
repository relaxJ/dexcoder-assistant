/**
 * 
 */
package com.dexcoder.assistant.office;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;

import com.dexcoder.assistant.exceptions.AssistantException;

/**
 * excel解析工具，兼容03,07以上格式
 * 
 * @author liyd
 * 
 */
public class ExcelReadTools {

    /**
     * 解析excel文件
     * 
     * @param file
     * @return
     */
    public static List<ExcelSheet> read(File file) {

        byte[] bytes = readFileToByte(file);
        return read(bytes);
    }

    /**
     * 解析excel文件
     *
     * @param bytes
     * @return
     */
    public static List<ExcelSheet> read(byte[] bytes) {

        Workbook workbook = readWorkbook(bytes);

        int numberOfSheets = workbook.getNumberOfSheets();
        List<ExcelSheet> sheetList = new ArrayList<ExcelSheet>(numberOfSheets);
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            ExcelSheet excelSheet = readSheet(sheet);
            sheetList.add(excelSheet);
        }
        return sheetList;
    }

    /**
     * 解析第一个sheet
     * 
     * @param file
     * @return
     */
    public static ExcelSheet readFirstSheet(File file) {

        byte[] bytes = readFileToByte(file);
        return readFirstSheet(bytes);
    }

    /**
     * 解析第一个sheet
     *
     * @param bytes
     * @return
     */
    public static ExcelSheet readFirstSheet(byte[] bytes) {

        Workbook workbook = readWorkbook(bytes);
        Sheet sheet = workbook.getSheetAt(0);
        return readSheet(sheet);
    }

    /**
     * 解析sheet
     * 
     * @param sheet
     * @return
     */
    private static ExcelSheet readSheet(Sheet sheet) {

        if (sheet == null) {
            return null;
        }

        ExcelSheet excelSheet = new ExcelSheet();
        excelSheet.setSheetName(sheet.getSheetName());

        int rowsCount = sheet.getLastRowNum();
        List<ExcelRow> rows = new ArrayList<ExcelRow>(rowsCount);
        for (int i = 0; i <= rowsCount; i++) {

            Row row = sheet.getRow(i);

            ExcelRow excelRow = readRow(row);

            rows.add(excelRow);
        }
        excelSheet.setRows(rows);
        return excelSheet;
    }

    /**
     * 解析row
     * 
     * @param row
     */
    private static ExcelRow readRow(Row row) {

        if (row == null) {
            return null;
        }

        int cellCount = row.getLastCellNum();
        List<ExcelCell> excelCells = new ArrayList<ExcelCell>(cellCount);
        for (int i = 0; i < cellCount; i++) {

            Cell cell = row.getCell(i);

            ExcelCell excelCell = readCell(cell);

            excelCells.add(excelCell);
        }
        ExcelRow excelRow = new ExcelRow();
        excelRow.setCells(excelCells);
        return excelRow;
    }

    /**
     * 解析cell
     * 
     * @param cell
     * @return
     */
    private static ExcelCell readCell(Cell cell) {

        if (cell == null) {
            return null;
        }
        int type = cell.getCellType();
        Object value;
        switch (type) {

            case Cell.CELL_TYPE_FORMULA:
                value = cell.getCellFormula();
                break;

            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;

            case Cell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    value = StringUtils.replace(
                        DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"), " 00:00:00", "");
                } else {
                    value = cell.getNumericCellValue();
                    DecimalFormat df = new DecimalFormat("0");
                    value = df.format(value);
                }
                break;

            default:
                value = cell.getStringCellValue();
                break;
        }

        ExcelCell excelCell = new ExcelCell();
        excelCell.setValue(value);
        return excelCell;
    }

    /**
     * 读取workbook
     *
     * @param bytes the bytes
     * @return workbook
     */
    private static Workbook readWorkbook(byte[] bytes) {

        Workbook workbook;

        InputStream is = null;

        try {
            is = new ByteArrayInputStream(bytes);
            workbook = WorkbookFactory.create(is);
        } catch (Exception e) {
            throw new AssistantException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return workbook;
    }

    /**
     * 将文件转换成byte数组
     *
     * @param file
     * @return
     */
    private static byte[] readFileToByte(File file) {
        try {
            byte[] bytes = FileUtils.readFileToByteArray(file);
            return bytes;
        } catch (IOException e) {
            throw new AssistantException("将文件转换成byte数组失败");
        }
    }

    public static void main(String[] args) {

        ExcelSheet excelSheet = ExcelReadTools.readFirstSheet(new File("e:/old/test.xlsx"));
        System.out.println(excelSheet);

    }

}