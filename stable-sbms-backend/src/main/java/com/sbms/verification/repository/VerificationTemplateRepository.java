package com.sbms.verification.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.verification.entity.VerificationTemplate;
import com.sbms.verification.enums.ChannelType;
import com.sbms.verification.enums.VerificationPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTemplateRepository extends JpaRepository<VerificationTemplate, Long> {
    List<VerificationTemplate> findByStatusOrderByTemplateNameAsc(RecordStatus status);
    Optional<VerificationTemplate> findFirstByPurposeAndChannelTypeAndStatus(VerificationPurpose purpose, ChannelType channelType, RecordStatus status);
}
