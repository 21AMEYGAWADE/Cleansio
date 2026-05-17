package com.datacleaner.service;

import com.datacleaner.model.PreviewResponse;
import com.datacleaner.model.SummaryResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class ExcelService {

    // CLEANING OPTIONS
    private boolean properCaseEnabled;
    private boolean removeDuplicatesEnabled;
    private boolean removeEmptyRowsEnabled;
    private boolean removeEmptyColumnsEnabled;
    private boolean trimSpacesEnabled;

    // =========================
    // PREVIEW FILE
    // =========================
    public PreviewResponse preview(MultipartFile file) {

        List<List<String>> data = new ArrayList<>();
        List<Integer> duplicateRows = new ArrayList<>();

        try {

            Workbook workbook =
                    new XSSFWorkbook(file.getInputStream());

            Sheet sheet = workbook.getSheetAt(0);

            Set<String> uniqueRows = new HashSet<>();

            int duplicates = 0;

            for (Row row : sheet) {

                List<String> rowData = new ArrayList<>();

                int lastColumn = row.getLastCellNum();

                for (int i = 0; i < lastColumn; i++) {

                    Cell cell = row.getCell(
                            i,
                            Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                    );

                    rowData.add(getRawCellValue(cell));
                }

                String rowKey = String.join("|", rowData);

                if (uniqueRows.contains(rowKey)) {

                    duplicateRows.add(data.size());
                    duplicates++;
                }

                uniqueRows.add(rowKey);

                data.add(rowData);
            }

            SummaryResponse summary = new SummaryResponse(
                    data.size(),
                    data.size() - duplicates,
                    duplicates,
                    0
            );

            workbook.close();

            return new PreviewResponse(
                    data,
                    duplicateRows,
                    summary
            );

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    // =========================
    // CLEAN FILE
    // =========================
    public ByteArrayInputStream clean(

            MultipartFile file,

            boolean properCase,
            boolean removeDuplicates,
            boolean removeEmptyRows,
            boolean removeEmptyColumns,
            boolean trimSpaces
    ) {

        this.properCaseEnabled = properCase;
        this.removeDuplicatesEnabled = removeDuplicates;
        this.removeEmptyRowsEnabled = removeEmptyRows;
        this.removeEmptyColumnsEnabled = removeEmptyColumns;
        this.trimSpacesEnabled = trimSpaces;

        try (

                Workbook workbook =
                        new XSSFWorkbook(file.getInputStream());

                Workbook cleanedWorkbook =
                        new XSSFWorkbook()

        ) {

            Sheet sheet = workbook.getSheetAt(0);

            Sheet cleanedSheet =
                    cleanedWorkbook.createSheet("Cleaned Data");

            List<Integer> validColumns;

            // REMOVE EMPTY COLUMNS
            if (removeEmptyColumnsEnabled) {

                validColumns = getNonEmptyColumns(sheet);

            } else {

                validColumns = new ArrayList<>();

                Row header = sheet.getRow(0);

                for (int i = 0;
                     i < header.getLastCellNum();
                     i++) {

                    validColumns.add(i);
                }
            }

            Set<String> uniqueRows = new HashSet<>();

            int cleanedRowIndex = 0;

            for (Row row : sheet) {

                // REMOVE EMPTY ROWS
                if (removeEmptyRowsEnabled &&
                        isRowEmpty(row)) {

                    continue;
                }

                String rowKey =
                        rowToString(row, validColumns);

                // REMOVE DUPLICATES
                if (removeDuplicatesEnabled &&
                        uniqueRows.contains(rowKey)) {

                    continue;
                }

                uniqueRows.add(rowKey);

                Row cleanedRow =
                        cleanedSheet.createRow(cleanedRowIndex++);

                copyRow(row, cleanedRow, validColumns);
            }

            // AUTO SIZE COLUMNS
            for (int i = 0;
                 i < validColumns.size();
                 i++) {

                cleanedSheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            cleanedWorkbook.write(out);

            return new ByteArrayInputStream(
                    out.toByteArray()
            );

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    // =========================
    // GET NON EMPTY COLUMNS
    // =========================
    private List<Integer> getNonEmptyColumns(
            Sheet sheet
    ) {

        List<Integer> validColumns =
                new ArrayList<>();

        Row headerRow = sheet.getRow(0);

        if (headerRow == null) {
            return validColumns;
        }

        int totalColumns =
                headerRow.getLastCellNum();

        for (int col = 0;
             col < totalColumns;
             col++) {

            boolean hasData = false;

            for (int rowIndex = 1;
                 rowIndex <= sheet.getLastRowNum();
                 rowIndex++) {

                Row row = sheet.getRow(rowIndex);

                if (row == null) continue;

                Cell cell = row.getCell(
                        col,
                        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                );

                String value = getCellValue(cell);

                if (!value.isEmpty()) {

                    hasData = true;
                    break;
                }
            }

            if (hasData) {

                validColumns.add(col);
            }
        }

        return validColumns;
    }

    // =========================
    // CHECK EMPTY ROW
    // =========================
    private boolean isRowEmpty(Row row) {

        if (row == null) {
            return true;
        }

        for (Cell cell : row) {

            if (!getCellValue(cell).isEmpty()) {

                return false;
            }
        }

        return true;
    }

    // =========================
    // COPY ROW
    // =========================
    private void copyRow(

            Row source,
            Row target,
            List<Integer> validColumns

    ) {

        int newCol = 0;

        for (Integer col : validColumns) {

            Cell oldCell = source.getCell(
                    col,
                    Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
            );

            Cell newCell =
                    target.createCell(newCol++);

            newCell.setCellValue(
                    getCellValue(oldCell)
            );
        }
    }

    // =========================
    // ROW TO STRING
    // =========================
    private String rowToString(

            Row row,
            List<Integer> validColumns

    ) {

        StringBuilder sb =
                new StringBuilder();

        for (Integer col : validColumns) {

            Cell cell = row.getCell(
                    col,
                    Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
            );

            sb.append(
                    getCellValue(cell)
            ).append("|");
        }

        return sb.toString();
    }

    // =========================
    // CLEAN CELL VALUE
    // =========================
    private String getCellValue(Cell cell) {

        if (cell == null) {
            return "";
        }

        String value = cell.toString();

        // TRIM SPACES
        if (trimSpacesEnabled) {

            value = value
                    .replaceAll("\\s+", " ")
                    .trim();
        }

        // PROPER CASE
        if (properCaseEnabled) {

            // IGNORE EMAILS / IDs
            if (!value.contains("@")
                    && !value.matches(".*\\d.*")) {

                value = toProperCase(value);
            }
        }

        return value;
    }

    // =========================
    // RAW CELL VALUE (PREVIEW)
    // =========================
    private String getRawCellValue(Cell cell) {

        if (cell == null) {
            return "";
        }

        return cell.toString();
    }

    // =========================
    // PROPER CASE CONVERTER
    // =========================
    private String toProperCase(String text) {

        if (text == null || text.isEmpty()) {
            return "";
        }

        String[] words =
                text.toLowerCase().split(" ");

        StringBuilder result =
                new StringBuilder();

        for (String word : words) {

            if (word.isEmpty()) continue;

            result.append(
                    Character.toUpperCase(
                            word.charAt(0)
                    )
            );

            if (word.length() > 1) {

                result.append(
                        word.substring(1)
                );
            }

            result.append(" ");
        }

        return result.toString().trim();
    }
}