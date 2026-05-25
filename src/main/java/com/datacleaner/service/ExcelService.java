package com.datacleaner.service;

import com.datacleaner.model.PreviewResponse;
import com.datacleaner.model.SummaryResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class ExcelService {

    private boolean properCaseEnabled;
    private boolean removeDuplicatesEnabled;
    private boolean removeEmptyRowsEnabled;
    private boolean removeEmptyColumnsEnabled;
    private boolean trimSpacesEnabled;

    // =========================
    // PREVIEW
    // =========================
    public PreviewResponse preview(MultipartFile file) {

        List<List<String>> data = new ArrayList<>();
        List<Integer> duplicateRows = new ArrayList<>();

        try {

            String extension =
                    getFileExtension(
                            file.getOriginalFilename()
                    );

            // XLSX
            if (extension.equals("xlsx")) {

                Workbook workbook =
                        new XSSFWorkbook(
                                file.getInputStream()
                        );

                Sheet sheet =
                        workbook.getSheetAt(0);

                for (Row row : sheet) {

                    List<String> rowData =
                            new ArrayList<>();

                    int lastColumn =
                            row.getLastCellNum();

                    for (int i = 0;
                         i < lastColumn;
                         i++) {

                        Cell cell = row.getCell(
                                i,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        );

                        rowData.add(
                                getRawCellValue(cell)
                        );
                    }

                    data.add(rowData);
                }

                workbook.close();
            }

            // CSV
            else if (extension.equals("csv")) {

                data =
                        readCSV(
                                file.getInputStream()
                        );
            }

            // JSON
            else if (extension.equals("json")) {

                data =
                        readJSON(
                                file.getInputStream()
                        );
            }

            // XML
            else if (extension.equals("xml")) {

                data =
                        readXML(
                                file.getInputStream()
                        );
            }

            Set<String> uniqueRows =
                    new HashSet<>();

            int duplicates = 0;

            for (int i = 0;
                 i < data.size();
                 i++) {

                String rowKey =
                        String.join("|", data.get(i));

                if (uniqueRows.contains(rowKey)) {

                    duplicateRows.add(i);
                    duplicates++;
                }

                uniqueRows.add(rowKey);
            }

            int qualityScore =
                    calculateQualityScore(
                            data.size(),
                            duplicates,
                            0,
                            0
                    );

            SummaryResponse summary =
                    new SummaryResponse(
                            data.size(),
                            data.size() - duplicates,
                            duplicates,
                            qualityScore
                    );

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
        // CLEAN
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

        try {

                String extension =
                        getFileExtension(
                                file.getOriginalFilename()
                        );

                List<List<String>> data =
                        new ArrayList<>();

                // ================= XLSX =================

                if (extension.equals("xlsx")) {

                Workbook workbook =
                        new XSSFWorkbook(
                                file.getInputStream()
                        );

                Sheet sheet =
                        workbook.getSheetAt(0);

                for (Row row : sheet) {

                        List<String> rowData =
                                new ArrayList<>();

                        int lastColumn =
                                row.getLastCellNum();

                        for (int i = 0;
                        i < lastColumn;
                        i++) {

                        Cell cell = row.getCell(
                                i,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        );

                        rowData.add(
                                getCellValue(cell)
                        );
                        }

                        data.add(rowData);
                }

                workbook.close();
                }

                // ================= CSV =================

                else if (extension.equals("csv")) {

                List<List<String>> csvData =
                        readCSV(file.getInputStream());

                for (List<String> row : csvData) {

                        List<String> cleanedRow =
                                new ArrayList<>();

                        for (String value : row) {

                        cleanedRow.add(
                                cleanTextValue(value)
                        );
                        }

                        data.add(cleanedRow);
                }
                }

                // ================= JSON =================

                else if (extension.equals("json")) {

                List<List<String>> jsonData =
                        readJSON(file.getInputStream());

                for (List<String> row : jsonData) {

                        List<String> cleanedRow =
                                new ArrayList<>();

                        for (String value : row) {

                        cleanedRow.add(
                                cleanTextValue(value)
                        );
                        }

                        data.add(cleanedRow);
                }
                }

                // ================= XML =================

                else if (extension.equals("xml")) {

                List<List<String>> xmlData =
                        readXML(file.getInputStream());

                for (List<String> row : xmlData) {

                        List<String> cleanedRow =
                                new ArrayList<>();

                        for (String value : row) {

                        cleanedRow.add(
                                cleanTextValue(value)
                        );
                        }

                        data.add(cleanedRow);
                }
                }

                // ================= REMOVE EMPTY COLUMNS =================

                if (removeEmptyColumnsEnabled && !data.isEmpty()) {

                List<Integer> validColumns =
                        new ArrayList<>();

                int totalColumns =
                        data.get(0).size();

                for (int col = 0;
                        col < totalColumns;
                        col++) {

                        boolean hasData = false;

                        for (List<String> row : data) {

                        if (col < row.size()) {

                                String value =
                                        row.get(col);

                                if (
                                        value != null &&
                                        !value.trim().isEmpty()
                                ) {

                                hasData = true;
                                break;
                                }
                        }
                        }

                        if (hasData) {

                        validColumns.add(col);
                        }
                }

                List<List<String>> updatedData =
                        new ArrayList<>();

                for (List<String> row : data) {

                        List<String> newRow =
                                new ArrayList<>();

                        for (Integer col : validColumns) {

                        if (col < row.size()) {

                                newRow.add(row.get(col));

                        } else {

                                newRow.add("");
                        }
                        }

                        updatedData.add(newRow);
                }

                data = updatedData;
                }

                // ================= CLEAN ROWS =================

                List<List<String>> cleanedData =
                        new ArrayList<>();

                Set<String> uniqueRows =
                        new HashSet<>();

                for (List<String> row : data) {

                // EMPTY ROW CHECK

                boolean empty = true;

                for (String cell : row) {

                        if (
                                cell != null &&
                                !cell.trim().isEmpty()
                        ) {

                        empty = false;
                        break;
                        }
                }

                if (
                        removeEmptyRowsEnabled &&
                        empty
                ) {
                        continue;
                }

                // DUPLICATE CHECK

                String rowKey =
                        String.join("|", row);

                if (
                        removeDuplicatesEnabled &&
                        uniqueRows.contains(rowKey)
                ) {
                        continue;
                }

                uniqueRows.add(rowKey);

                cleanedData.add(row);
                }

                // ================= EXPORT TO EXCEL =================

                Workbook cleanedWorkbook =
                        new XSSFWorkbook();

                Sheet cleanedSheet =
                        cleanedWorkbook.createSheet(
                                "Cleaned Data"
                        );

                for (int i = 0;
                i < cleanedData.size();
                i++) {

                Row row =
                        cleanedSheet.createRow(i);

                List<String> rowData =
                        cleanedData.get(i);

                for (int j = 0;
                        j < rowData.size();
                        j++) {

                        Cell cell =
                                row.createCell(j);

                        cell.setCellValue(
                                rowData.get(j)
                        );
                }
                }

                // AUTO SIZE

                if (!cleanedData.isEmpty()) {

                for (int i = 0;
                        i < cleanedData.get(0).size();
                        i++) {

                        cleanedSheet.autoSizeColumn(i);
                }
                }

                ByteArrayOutputStream out =
                        new ByteArrayOutputStream();

                cleanedWorkbook.write(out);

                cleanedWorkbook.close();

                return new ByteArrayInputStream(
                        out.toByteArray()
                );

        } catch (Exception e) {

                e.printStackTrace();

                throw new RuntimeException(e);
        }
        }


        private String cleanTextValue(String value) {

    if (value == null) {
        return "";
    }

    // TRIM SPACES

    if (trimSpacesEnabled) {

        value = value
                .replaceAll("\\s+", " ")
                .trim();
    }

    // PROPER CASE

    if (properCaseEnabled) {

        if (
                !value.contains("@") &&
                !value.matches(".*\\d.*")
        ) {

            value = toProperCase(value);
        }
    }

    // EMAIL VALIDATION

    if (value.contains("@")) {

        if (!isValidEmail(value)) {

            value = "INVALID_EMAIL";
        }
    }

    return value;
}

    // =========================
    // NON EMPTY COLUMNS
    // =========================
    private List<Integer> getNonEmptyColumns(
            Sheet sheet
    ) {

        List<Integer> validColumns =
                new ArrayList<>();

        Row headerRow =
                sheet.getRow(0);

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

                Row row =
                        sheet.getRow(rowIndex);

                if (row == null) continue;

                Cell cell = row.getCell(
                        col,
                        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                );

                String value =
                        getCellValue(cell);

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

    private String generateInsights(List<List<String>> data) {
        int rows = data.size();

        return """
            Dataset Analysis:

            • Total rows: %d
            • Dataset quality: Good
            • Duplicate rows detected
            • Missing values cleaned
            • Recommended for analytics
            """.formatted(rows);
    }


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

    private String getCellValue(Cell cell) {
        if (cell == null) {
                return "";
        }

        return cleanTextValue(cell.toString());
        }
    private String getRawCellValue(Cell cell) {

        if (cell == null) {
            return "";
        }

        return cell.toString();
    }

    // =========================
    // FILE TYPE
    // =========================
    private String getFileExtension(
            String filename
    ) {

        if (filename == null) {
            return "";
        }

        return filename.substring(
                filename.lastIndexOf(".") + 1
        ).toLowerCase();
    }

    // =========================
    // CSV
    // =========================
    private List<List<String>> readCSV(
            InputStream inputStream
    ) throws IOException {

        List<List<String>> data =
                new ArrayList<>();

        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(inputStream)
                );

        String line;

        while ((line = br.readLine()) != null) {

            data.add(
                    Arrays.asList(line.split(","))
            );
        }

        return data;
    }

    // =========================
    // JSON
    // =========================
    private List<List<String>> readJSON(
            InputStream inputStream
    ) throws Exception {

        ObjectMapper mapper =
                new ObjectMapper();

        List<Map<String, Object>> jsonData =
                mapper.readValue(
                        inputStream,
                        List.class
                );

        List<List<String>> result =
                new ArrayList<>();

        for (Map<String, Object> row : jsonData) {

            List<String> values =
                    new ArrayList<>();

            for (Object value : row.values()) {

                values.add(
                        String.valueOf(value)
                );
            }

            result.add(values);
        }

        return result;
    }

    // =========================
    // XML
    // =========================
    private List<List<String>> readXML(
            InputStream inputStream
    ) throws Exception {

        XmlMapper xmlMapper =
                new XmlMapper();

        Map<String, Object> xmlData =
                xmlMapper.readValue(
                        inputStream,
                        Map.class
                );

        List<List<String>> result =
                new ArrayList<>();

        for (Object value : xmlData.values()) {

            List<String> row =
                    new ArrayList<>();

            row.add(
                    String.valueOf(value)
            );

            result.add(row);
        }

        return result;
    }

    // =========================
    // EMAIL VALIDATION
    // =========================
    private boolean isValidEmail(
            String email
    ) {

        String regex =
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        return email.matches(regex);
    }

    // =========================
    // QUALITY SCORE
    // =========================
    private int calculateQualityScore(

            int totalRows,
            int duplicateRows,
            int emptyRows,
            int invalidEmails

    ) {

        int score = 100;

        score -= duplicateRows * 2;
        score -= emptyRows * 2;
        score -= invalidEmails * 3;

        return Math.max(score, 0);
    }

    // =========================
    // PROPER CASE
    // =========================
    private String toProperCase(
            String text
    ) {

        if (
                text == null
                        || text.isEmpty()
        ) {
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