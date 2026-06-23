package com.sbms.contract.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;

public class ContractTemplateResponse {
    private final Long id;
    private final String templateCode;
    private final String templateName;
    private final String contractType;
    private final Integer versionNo;
    private final String templateBody;
    private final RecordStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long generatedContractCount;

    public ContractTemplateResponse(Long id, String templateCode, String templateName, String contractType, Integer versionNo,
                                    String templateBody, RecordStatus status, LocalDateTime createdAt, LocalDateTime updatedAt,
                                    Long generatedContractCount) {
        this.id = id;
        this.templateCode = templateCode;
        this.templateName = templateName;
        this.contractType = contractType;
        this.versionNo = versionNo;
        this.templateBody = templateBody;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.generatedContractCount = generatedContractCount;
    }

    public Long getId() { return id; }
    public String getTemplateCode() { return templateCode; }
    public String getTemplateName() { return templateName; }
    public String getContractType() { return contractType; }
    public Integer getVersionNo() { return versionNo; }
    public String getTemplateBody() { return templateBody; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getGeneratedContractCount() { return generatedContractCount; }
}
