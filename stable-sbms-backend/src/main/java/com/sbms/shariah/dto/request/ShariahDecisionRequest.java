package com.sbms.shariah.dto.request;

import java.util.ArrayList;
import java.util.List;

public class ShariahDecisionRequest {
    private String decisionBy;
    private String remarks;
    private List<ShariahChecklistSelectionRequest> checklistItems = new ArrayList<>();

    public String getDecisionBy() { return decisionBy; }
    public void setDecisionBy(String decisionBy) { this.decisionBy = decisionBy; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public List<ShariahChecklistSelectionRequest> getChecklistItems() { return checklistItems; }
    public void setChecklistItems(List<ShariahChecklistSelectionRequest> checklistItems) { this.checklistItems = checklistItems; }
}
