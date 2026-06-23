package com.sbms.atm.dto.response;

public class AtmTerminalDropdownResponse {

    private Long id;
    private String terminalCode;
    private String terminalName;

    public AtmTerminalDropdownResponse(Long id, String terminalCode, String terminalName) {
        this.id = id;
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
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
}