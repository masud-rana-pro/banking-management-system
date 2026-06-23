package com.sbms.report.dto.response;

public class ReportMetricResponse {

    private String label;
    private String value;
    private String tone;

    public ReportMetricResponse(String label, String value, String tone) {
        this.label = label;
        this.value = value;
        this.tone = tone;
    }

    public String getLabel() { return label; }
    public String getValue() { return value; }
    public String getTone() { return tone; }
}
