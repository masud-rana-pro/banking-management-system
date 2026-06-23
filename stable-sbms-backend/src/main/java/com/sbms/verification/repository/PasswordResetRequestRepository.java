package com.sbms.verification.repository;

import com.sbms.verification.entity.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, Long> {
    Optional<PasswordResetRequest> findByRequestId(Long requestId);
    List<PasswordResetRequest> findTop10ByOrderByIdDesc();
}
