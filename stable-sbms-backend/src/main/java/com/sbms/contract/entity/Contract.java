package com.sbms.contract.entity;

import com.sbms.contract.enums.ContractStatus;
import com.sbms.contract.enums.ContractType;
import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contract", uniqueConstraints = {
        @UniqueConstraint(name = "uk_contract_no", columnNames = "contract_no")
})
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_no", nullable = false, length = 40)
    private String contractNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false, length = 40)
    private ContractType contractType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private ContractTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "reference_module", nullable = false, length = 80)
    private String referenceModule;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Lob
    @Column(name = "contract_text", nullable = false, columnDefinition = "LONGTEXT")
    private String contractText;

    @Column(name = "supporting_document_name", length = 255)
    private String supportingDocumentName;

    @Column(name = "signed_by_customer", length = 160)
    private String signedByCustomer;

    @Column(name = "signed_by_shariah", length = 160)
    private String signedByShariah;

    @Column(name = "customer_signed_at")
    private LocalDateTime customerSignedAt;

    @Column(name = "shariah_signed_at")
    private LocalDateTime shariahSignedAt;

    @Column(name = "signed_date")
    private LocalDateTime signedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_status", nullable = false, length = 30)
    private ContractStatus contractStatus = ContractStatus.DRAFT;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
        if (contractStatus == null) {
            contractStatus = ContractStatus.DRAFT;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }
    public ContractType getContractType() { return contractType; }
    public void setContractType(ContractType contractType) { this.contractType = contractType; }
    public ContractTemplate getTemplate() { return template; }
    public void setTemplate(ContractTemplate template) { this.template = template; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public String getReferenceModule() { return referenceModule; }
    public void setReferenceModule(String referenceModule) { this.referenceModule = referenceModule; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public String getContractText() { return contractText; }
    public void setContractText(String contractText) { this.contractText = contractText; }
    public String getSupportingDocumentName() { return supportingDocumentName; }
    public void setSupportingDocumentName(String supportingDocumentName) { this.supportingDocumentName = supportingDocumentName; }
    public String getSignedByCustomer() { return signedByCustomer; }
    public void setSignedByCustomer(String signedByCustomer) { this.signedByCustomer = signedByCustomer; }
    public String getSignedByShariah() { return signedByShariah; }
    public void setSignedByShariah(String signedByShariah) { this.signedByShariah = signedByShariah; }
    public LocalDateTime getCustomerSignedAt() { return customerSignedAt; }
    public void setCustomerSignedAt(LocalDateTime customerSignedAt) { this.customerSignedAt = customerSignedAt; }
    public LocalDateTime getShariahSignedAt() { return shariahSignedAt; }
    public void setShariahSignedAt(LocalDateTime shariahSignedAt) { this.shariahSignedAt = shariahSignedAt; }
    public LocalDateTime getSignedDate() { return signedDate; }
    public void setSignedDate(LocalDateTime signedDate) { this.signedDate = signedDate; }
    public ContractStatus getContractStatus() { return contractStatus; }
    public void setContractStatus(ContractStatus contractStatus) { this.contractStatus = contractStatus; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
