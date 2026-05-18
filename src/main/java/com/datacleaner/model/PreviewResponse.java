package com.datacleaner.model;

import java.util.List;

public class PreviewResponse {

    private List<List<String>> data;
    private List<Integer> duplicateRows;
    private SummaryResponse summary;

    public PreviewResponse() {
    }

    public PreviewResponse(
            List<List<String>> data,
            List<Integer> duplicateRows,
            SummaryResponse summary
    ) {
        this.data = data;
        this.duplicateRows = duplicateRows;
        this.summary = summary;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }

    public List<Integer> getDuplicateRows() {
        return duplicateRows;
    }

    public void setDuplicateRows(List<Integer> duplicateRows) {
        this.duplicateRows = duplicateRows;
    }

    public SummaryResponse getSummary() {
        return summary;
    }

    public void setSummary(SummaryResponse summary) {
        this.summary = summary;
    }
}