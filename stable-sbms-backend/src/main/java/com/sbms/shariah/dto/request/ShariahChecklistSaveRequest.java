package com.sbms.shariah.dto.request;

import java.util.ArrayList;
import java.util.List;

public class ShariahChecklistSaveRequest {
    private String reviewedBy;
    private String remarks;
    private List<ShariahChecklistSelectionRequest> checklistItems = new ArrayList<>();

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public List<ShariahChecklistSelectionRequest> getChecklistItems() { return checklistItems; }
    public void setChecklistItems(List<ShariahChecklistSelectionRequest> checklistItems) { this.checklistItems = checklistItems; }
}
