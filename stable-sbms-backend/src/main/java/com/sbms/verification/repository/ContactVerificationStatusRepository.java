package com.sbms.verification.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.verification.entity.ContactVerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactVerificationStatusRepository extends JpaRepository<ContactVerificationStatus, Long> {
    List<ContactVerificationStatus> findTop10ByStatusOrderByUpdatedAtDesc(RecordStatus status);
    List<ContactVerificationStatus> findByStatusOrderByUpdatedAtDesc(RecordStatus status);
    List<ContactVerificationStatus> findByReferenceModuleIgnoreCaseAndReferenceIdAndStatusOrderByUpdatedAtDesc(String referenceModule, Long referenceId, RecordStatus status);
    Optional<ContactVerificationStatus> findTopByReferenceModuleAndReferenceIdAndContactTypeAndContactValueAndStatusOrderByIdDesc(
            String referenceModule, Long referenceId, String contactType, String contactValue, RecordStatus status
    );
    long countByContactTypeAndIsVerifiedAndStatus(String contactType, Boolean isVerified, RecordStatus status);
}
