package com.sbms.verification.repository;

import com.sbms.verification.entity.OtpVerificationRequest;
import com.sbms.verification.enums.VerificationPurpose;
import com.sbms.verification.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface OtpVerificationRequestRepository extends JpaRepository<OtpVerificationRequest, Long> {

    @Query("SELECT r FROM OtpVerificationRequest r " +
            "LEFT JOIN FETCH r.user u " +
            "LEFT JOIN FETCH r.customer c " +
            "ORDER BY r.id DESC")
    List<OtpVerificationRequest> findAllWithRelations();

    @Query("SELECT r FROM OtpVerificationRequest r " +
            "LEFT JOIN FETCH r.user u " +
            "LEFT JOIN FETCH r.customer c " +
            "WHERE r.id = :id")
    OtpVerificationRequest findDetailsById(Long id);

    @Query("SELECT r FROM OtpVerificationRequest r " +
            "WHERE LOWER(r.contactValue) = LOWER(:contactValue) " +
            "AND r.purpose = :purpose " +
            "AND r.requestStatus IN :statuses " +
            "AND r.expiresAt > :now " +
            "ORDER BY r.id DESC")
    List<OtpVerificationRequest> findActiveForContact(String contactValue, VerificationPurpose purpose,
                                                      Collection<VerificationStatus> statuses, LocalDateTime now);

    List<OtpVerificationRequest> findTop8ByOrderByIdDesc();

    long countByRequestStatus(VerificationStatus requestStatus);

    @Query("SELECT COUNT(r.id) FROM OtpVerificationRequest r WHERE r.expiresAt > :now AND r.requestStatus IN :statuses")
    long countPendingActive(LocalDateTime now, Collection<VerificationStatus> statuses);
}
