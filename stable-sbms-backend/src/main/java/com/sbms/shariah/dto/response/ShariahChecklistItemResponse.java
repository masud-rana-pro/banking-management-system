package com.sbms.shariah.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;

public class ShariahChecklistItemResponse {
    private final Long id;
    private final String itemCode;
    private final String itemName;
    private final String description;
    private final RecordStatus status;
    private final Boolean selected;
    private final String note;
    private final LocalDateTime createdAt;

    public ShariahChecklistItemResponse(Long id, String itemCode, String itemName, String description,
                                        RecordStatus status, Boolean selected, String note, LocalDateTime createdAt) {
        this.id = id;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.description = description;
        this.status = status;
        this.selected = selected;
        this.note = note;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getItemCode() { return itemCode; }
    public String getItemName() { return itemName; }
    public String getDescription() { return description; }
    public RecordStatus getStatus() { return status; }
    public Boolean getSelected() { return selected; }
    public String getNote() { return note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
