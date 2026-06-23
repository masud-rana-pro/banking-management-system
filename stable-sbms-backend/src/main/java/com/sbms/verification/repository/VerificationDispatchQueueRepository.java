package com.sbms.verification.repository;

import com.sbms.verification.entity.VerificationDispatchQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationDispatchQueueRepository extends JpaRepository<VerificationDispatchQueue, Long> {
    List<VerificationDispatchQueue> findTop20ByOrderByIdDesc();
    List<VerificationDispatchQueue> findByRequestIdOrderByIdDesc(Long requestId);
}
