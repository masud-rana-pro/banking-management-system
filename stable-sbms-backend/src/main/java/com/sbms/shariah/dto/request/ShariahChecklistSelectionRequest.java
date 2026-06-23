package com.sbms.shariah.dto.request;

public class ShariahChecklistSelectionRequest {
    private Long itemId;
    private Boolean selected;
    private String note;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Boolean getSelected() { return selected; }
    public void setSelected(Boolean selected) { this.selected = selected; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
