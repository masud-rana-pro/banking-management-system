package com.sbms.verification.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.verification.entity.VerificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationChannelRepository extends JpaRepository<VerificationChannel, Long> {
    List<VerificationChannel> findByStatusOrderByChannelNameAsc(RecordStatus status);
    Optional<VerificationChannel> findByChannelCode(String channelCode);
}
