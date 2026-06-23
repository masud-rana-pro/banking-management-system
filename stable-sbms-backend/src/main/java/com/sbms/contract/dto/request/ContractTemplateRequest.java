package com.sbms.contract.dto.request;

import com.sbms.contract.enums.ContractType;

public class ContractTemplateRequest {
    private String templateCode;
    private String templateName;
    private ContractType contractType;
    private Integer versionNo;
    private String templateBody;

    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public ContractType getContractType() { return contractType; }
    public void setContractType(ContractType contractType) { this.contractType = contractType; }
    public Integer getVersionNo() { return versionNo; }
    public void setVersionNo(Integer versionNo) { this.versionNo = versionNo; }
    public String getTemplateBody() { return templateBody; }
    public void setTemplateBody(String templateBody) { this.templateBody = templateBody; }
}
