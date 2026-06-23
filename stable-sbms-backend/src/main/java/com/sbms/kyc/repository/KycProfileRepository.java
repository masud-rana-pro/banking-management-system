package com.sbms.kyc.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.kyc.entity.KycProfile;
import com.sbms.kyc.enums.KycReviewStatus;
import com.sbms.kyc.enums.RiskLevel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class KycProfileRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public KycProfile save(KycProfile profile) {
        entityManager.persist(profile);
        return profile;
    }

    public KycProfile update(KycProfile profile) {
        return entityManager.merge(profile);
    }

    public Optional<KycProfile> findById(Long id) {
        try {
            KycProfile profile = entityManager.createQuery(
                            "SELECT p FROM KycProfile p JOIN FETCH p.customer c WHERE p.id = :id",
                            KycProfile.class
                    )
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(profile);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<KycProfile> findByCustomerId(Long customerId) {
        try {
            KycProfile profile = entityManager.createQuery(
                            "SELECT p FROM KycProfile p JOIN FETCH p.customer c WHERE c.id = :customerId AND p.status <> :archived",
                            KycProfile.class
                    )
                    .setParameter("customerId", customerId)
                    .setParameter("archived", RecordStatus.ARCHIVED)
                    .getSingleResult();
            return Optional.of(profile);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<KycProfile> findAll() {
        return entityManager.createQuery(
                        "SELECT p FROM KycProfile p JOIN FETCH p.customer c WHERE p.status <> :archived ORDER BY p.id DESC",
                        KycProfile.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getResultList();
    }

    public boolean existsByCustomerId(Long customerId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(p.id) FROM KycProfile p WHERE p.customer.id = :customerId AND p.status <> :archived",
                        Long.class
                )
                .setParameter("customerId", customerId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
        return count > 0;
    }

    public boolean existsByCustomerIdExceptId(Long customerId, Long id) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(p.id) FROM KycProfile p WHERE p.customer.id = :customerId AND p.id <> :id AND p.status <> :archived",
                        Long.class
                )
                .setParameter("customerId", customerId)
                .setParameter("id", id)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
        return count > 0;
    }

    public Long countByReviewStatuses(List<KycReviewStatus> statuses) {
        return entityManager.createQuery(
                        "SELECT COUNT(p.id) FROM KycProfile p WHERE p.reviewStatus IN :statuses AND p.status <> :archived",
                        Long.class
                )
                .setParameter("statuses", statuses)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public Long countByRiskLevel(RiskLevel riskLevel) {
        return entityManager.createQuery(
                        "SELECT COUNT(p.id) FROM KycProfile p WHERE p.riskLevel = :riskLevel AND p.status <> :archived",
                        Long.class
                )
                .setParameter("riskLevel", riskLevel)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }
}
