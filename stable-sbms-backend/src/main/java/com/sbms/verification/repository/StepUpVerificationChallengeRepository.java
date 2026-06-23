package com.sbms.verification.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.verification.entity.StepUpVerificationChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StepUpVerificationChallengeRepository extends JpaRepository<StepUpVerificationChallenge, Long> {

    Optional<StepUpVerificationChallenge> findTopByRequestIdOrderByIdDesc(Long requestId);

    Optional<StepUpVerificationChallenge> findTopByVerificationTokenAndStatusOrderByIdDesc(String verificationToken, RecordStatus status);

    List<StepUpVerificationChallenge> findByUserIdAndActionCodeIgnoreCaseAndTargetModuleIgnoreCaseAndTargetIdAndStatusOrderByIdDesc(
            Long userId, String actionCode, String targetModule, Long targetId, RecordStatus status
    );

    List<StepUpVerificationChallenge> findByUserIdAndStatusOrderByIdDesc(Long userId, RecordStatus status);

    default Optional<StepUpVerificationChallenge> findReusableActiveChallenge(Long userId, String actionCode, String targetModule, Long targetId) {
        return findByUserIdAndActionCodeIgnoreCaseAndTargetModuleIgnoreCaseAndTargetIdAndStatusOrderByIdDesc(
                userId, actionCode, targetModule, targetId, RecordStatus.ACTIVE
        ).stream().filter(item ->
                item.getConsumedAt() == null
                        && item.getRequest() != null
                        && item.getRequest().getExpiresAt() != null
                        && item.getRequest().getExpiresAt().isAfter(LocalDateTime.now())
        ).findFirst();
    }
}
