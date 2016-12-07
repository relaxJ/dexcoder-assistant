package com.dexcoder.commons.office;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.apache.commons.collections.CollectionUtils;

/**
 * excel sheet信息及数据
 * <p>
 * Created by liyd on 7/28/14.
 */
public class ExcelSheet {

    /**
     * sheet标题
     */
    private String         sheetName;

    /**
     * sheet中的所有行
     */
    private List<ExcelRow> rows;

    /**
     * 行标题，默认为第一行
     */
    private List<String>   rowTitles;

    /** 数据行，按习惯把第一行作为标题 */
    private List<ExcelRow> dataRows;

    public ExcelSheet(String sheetName) {
        this.sheetName = sheetName;
        this.rowTitles = new ArrayList<String>();
        this.rows = new ArrayList<ExcelRow>();
    }

    /**
     * 获取标题行
     *
     * @return
     */
    public List<String> getRowTitles() {

        if (CollectionUtils.isNotEmpty(rowTitles)) {
            return rowTitles;
        }
        if (CollectionUtils.isNotEmpty(rows) && !rows.iterator().next().isEmptyRow()) {
            List<ExcelCell> cells = rows.get(0).getCells();
            for (ExcelCell excelCell : cells) {
                rowTitles.add(excelCell.getStringValue());
            }
        }
        return rowTitles;
    }

    /**
     * 获取数据行
     *
     * @return
     */
    public List<ExcelRow> getDataRows(){

        if (CollectionUtils.isNotEmpty(dataRows)){
            return dataRows;
        }

        if (CollectionUtils.isNotEmpty(rows)){
            dataRows = rows.subList(1,rows.size());
        }

        return dataRows;
    }

    public void createRowTitles(String... titles) {
        this.rowTitles = Arrays.asList(titles);
    }

    public void createRowTitles(List<String> rowTitles) {

        this.rowTitles = rowTitles;
    }

    public void addRow(Object... values) {
        ExcelRow excelRow = new ExcelRow();
        excelRow.setCells(values);
        this.rows.add(excelRow);
    }

    public void addRow(List<Object> values) {
        ExcelRow excelRow = new ExcelRow();
        excelRow.setCells(values);
        this.rows.add(excelRow);
    }

    public void setRows(List<ExcelRow> rows) {
        this.rows = rows;
    }

    public int getTotalRowsNum() {
        return rows == null ? 0 : rows.size();
    }

    public ExcelRow getRow(int i) {
        return rows == null ? null : rows.get(i);
    }

    public boolean hasContent() {
        return !CollectionUtils.isEmpty(rowTitles) || hasRows();
    }

    public boolean hasRows() {
        if (rows == null) {
            return false;
        }
        boolean result = false;
        Iterator<ExcelRow> iterator = rows.iterator();
        while (iterator.hasNext()) {
            boolean emptyRow = iterator.next().isEmptyRow();
            if (!emptyRow) {
                result = true;
                break;
            }
        }
        return result;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<ExcelRow> getRows() {
        return rows;
    }

    @Override
    public String toString() {

        String lineSeparator = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append("sheetName:").append(sheetName);
        sb.append(lineSeparator);

        sb.append("rows:");
        if (rows == null) {
            sb.append("null");
        } else {

            sb.append("[").append(lineSeparator);
            for (ExcelRow row : rows) {
                sb.append(row == null ? "null" : row.toString());
                sb.append(lineSeparator);
            }
            sb.append("]");
        }
        return sb.toString();
    }
}
