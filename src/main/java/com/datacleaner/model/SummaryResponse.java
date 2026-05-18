package com.datacleaner.model;

public class SummaryResponse {

    private int totalRows;
    private int cleanedRows;
    private int duplicatesRemoved;
    private int qualityScore;
    private int columnsRemoved;

    public SummaryResponse() {
    }

    public SummaryResponse(
            int totalRows,
            int cleanedRows,
            int duplicatesRemoved,
            int qualityScore
    ) {
        this.totalRows = totalRows;
        this.cleanedRows = cleanedRows;
        this.duplicatesRemoved = duplicatesRemoved;
        this.qualityScore = qualityScore;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getCleanedRows() {
        return cleanedRows;
    }

    public void setCleanedRows(int cleanedRows) {
        this.cleanedRows = cleanedRows;
    }

    public int getDuplicatesRemoved() {
        return duplicatesRemoved;
    }

    public void setDuplicatesRemoved(int duplicatesRemoved) {
        this.duplicatesRemoved = duplicatesRemoved;
    }

    public int getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }

    public int getColumnsRemoved() {
        return columnsRemoved;
    }

    public void setColumnsRemoved(int columnsRemoved) {
        this.columnsRemoved = columnsRemoved;
    }
}