package com.sbms.accounting.entity;

import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "gl_account",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_gl_account_code", columnNames = "account_code")
        }
)
public class GlAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_code", nullable = false, length = 30)
    private String accountCode;

    @Column(name = "account_name", nullable = false, length = 150)
    private String accountName;

    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType;

    @Column(name = "parent_account_code", length = 30)
    private String parentAccountCode;

    @Column(name = "allow_posting", nullable = false)
    private Boolean allowPosting = true;

    @Column(name = "branch_scoped", nullable = false)
    private Boolean branchScoped = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
        if (allowPosting == null) {
            allowPosting = true;
        }
        if (branchScoped == null) {
            branchScoped = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getParentAccountCode() { return parentAccountCode; }
    public void setParentAccountCode(String parentAccountCode) { this.parentAccountCode = parentAccountCode; }

    public Boolean getAllowPosting() { return allowPosting; }
    public void setAllowPosting(Boolean allowPosting) { this.allowPosting = allowPosting; }

    public Boolean getBranchScoped() { return branchScoped; }
    public void setBranchScoped(Boolean branchScoped) { this.branchScoped = branchScoped; }

    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
