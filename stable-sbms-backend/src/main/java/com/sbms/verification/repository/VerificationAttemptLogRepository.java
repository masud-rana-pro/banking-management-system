package com.sbms.verification.repository;

import com.sbms.verification.entity.VerificationAttemptLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationAttemptLogRepository extends JpaRepository<VerificationAttemptLog, Long> {
    List<VerificationAttemptLog> findByRequestIdOrderByIdAsc(Long requestId);
    List<VerificationAttemptLog> findTop20ByOrderByIdDesc();
}
