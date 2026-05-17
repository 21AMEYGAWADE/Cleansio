package com.datacleaner.model;

public class SummaryResponse {

    private int totalRows;
    private int cleanedRows;
    private int duplicatesRemoved;
    private int columnsRemoved;

    public SummaryResponse(int totalRows,
                           int cleanedRows,
                           int duplicatesRemoved,
                           int columnsRemoved) {

        this.totalRows = totalRows;
        this.cleanedRows = cleanedRows;
        this.duplicatesRemoved = duplicatesRemoved;
        this.columnsRemoved = columnsRemoved;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getCleanedRows() {
        return cleanedRows;
    }

    public int getDuplicatesRemoved() {
        return duplicatesRemoved;
    }

    public int getColumnsRemoved() {
        return columnsRemoved;
    }
}