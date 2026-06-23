package com.sbms.lookup.entity;

import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lookup_value", uniqueConstraints = {
        @UniqueConstraint(name = "uk_lookup_type_value_code", columnNames = {"lookup_type_id", "value_code"})
})
public class LookupValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lookup_type_id", nullable = false)
    private LookupType lookupType;

    @Column(name = "value_code", nullable = false, length = 80)
    private String valueCode;

    @Column(name = "value_label", nullable = false, length = 160)
    private String valueLabel;

    @Column(name = "value_bn_label", length = 160)
    private String valueBnLabel;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "extra_data", length = 2000)
    private String extraData;

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
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (status == null) status = RecordStatus.ACTIVE;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public LookupType getLookupType() { return lookupType; }
    public void setLookupType(LookupType lookupType) { this.lookupType = lookupType; }
    public String getValueCode() { return valueCode; }
    public void setValueCode(String valueCode) { this.valueCode = valueCode; }
    public String getValueLabel() { return valueLabel; }
    public void setValueLabel(String valueLabel) { this.valueLabel = valueLabel; }
    public String getValueBnLabel() { return valueBnLabel; }
    public void setValueBnLabel(String valueBnLabel) { this.valueBnLabel = valueBnLabel; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getExtraData() { return extraData; }
    public void setExtraData(String extraData) { this.extraData = extraData; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
