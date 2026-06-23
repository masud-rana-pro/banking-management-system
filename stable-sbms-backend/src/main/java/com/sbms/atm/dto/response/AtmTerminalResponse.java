package com.sbms.atm.dto.response;

import com.sbms.atm.enums.TerminalStatus;
import com.sbms.atm.enums.TerminalType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AtmTerminalResponse {

    private Long id;
    private String terminalCode;
    private String terminalName;
    private TerminalType terminalType;
    private Long branchId;
    private String locationNote;
    private String ipAddress;
    private String serialNo;
    private String vendorName;
    private LocalDate installDate;
    private TerminalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AtmTerminalResponse(
            Long id,
            String terminalCode,
            String terminalName,
            TerminalType terminalType,
            Long branchId,
            String locationNote,
            String ipAddress,
            String serialNo,
            String vendorName,
            LocalDate installDate,
            TerminalStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
        this.terminalType = terminalType;
        this.branchId = branchId;
        this.locationNote = locationNote;
        this.ipAddress = ipAddress;
        this.serialNo = serialNo;
        this.vendorName = vendorName;
        this.installDate = installDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public TerminalType getTerminalType() {
        return terminalType;
    }

    public Long getBranchId() {
        return branchId;
    }

    public String getLocationNote() {
        return locationNote;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public String getVendorName() {
        return vendorName;
    }

    public LocalDate getInstallDate() {
        return installDate;
    }

    public TerminalStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}