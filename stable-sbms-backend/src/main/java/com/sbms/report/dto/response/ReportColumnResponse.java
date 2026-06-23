package com.sbms.report.dto.response;

public class ReportColumnResponse {

    private String key;
    private String label;

    public ReportColumnResponse(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public String getKey() { return key; }
    public String getLabel() { return label; }
}
