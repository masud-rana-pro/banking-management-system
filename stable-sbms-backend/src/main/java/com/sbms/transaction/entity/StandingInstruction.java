package com.sbms.transaction.entity;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.transaction.enums.StandingInstructionStatus;
import com.sbms.transaction.enums.TransferMode;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "standing_instruction",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_standing_instruction_code", columnNames = "instruction_code")
        }
)
public class StandingInstruction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instruction_code", nullable = false, length = 40)
    private String instructionCode;

    @Column(name = "from_account_id", nullable = false)
    private Long fromAccountId;

    @Column(name = "to_account_id", nullable = false)
    private Long toAccountId;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_mode", nullable = false, length = 30)
    private TransferMode transferMode = TransferMode.INTERNAL;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Column(name = "frequency", nullable = false, length = 30)
    private String frequency;

    @Column(name = "next_execution_date")
    private LocalDate nextExecutionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "instruction_status", nullable = false, length = 20)
    private StandingInstructionStatus instructionStatus = StandingInstructionStatus.ACTIVE;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (scheduleDate == null) {
            scheduleDate = LocalDate.now();
        }
        if (nextExecutionDate == null) {
            nextExecutionDate = scheduleDate;
        }
        if (instructionStatus == null) {
            instructionStatus = StandingInstructionStatus.ACTIVE;
        }
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
        if (transferMode == null) {
            transferMode = TransferMode.INTERNAL;
        }
    }

    public Long getId() {
        return id;
    }

    public String getInstructionCode() {
        return instructionCode;
    }

    public void setInstructionCode(String instructionCode) {
        this.instructionCode = instructionCode;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransferMode getTransferMode() {
        return transferMode;
    }

    public void setTransferMode(TransferMode transferMode) {
        this.transferMode = transferMode;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public LocalDate getNextExecutionDate() {
        return nextExecutionDate;
    }

    public void setNextExecutionDate(LocalDate nextExecutionDate) {
        this.nextExecutionDate = nextExecutionDate;
    }

    public StandingInstructionStatus getInstructionStatus() {
        return instructionStatus;
    }

    public void setInstructionStatus(StandingInstructionStatus instructionStatus) {
        this.instructionStatus = instructionStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
