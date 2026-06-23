package com.sbms.kyc.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.kyc.entity.KycDecisionHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class KycDecisionHistoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public KycDecisionHistory save(KycDecisionHistory history) {
        entityManager.persist(history);
        return history;
    }

    public List<KycDecisionHistory> findByKycProfileId(Long kycProfileId) {
        return entityManager.createQuery(
                        "SELECT h FROM KycDecisionHistory h JOIN FETCH h.kycProfile p " +
                                "WHERE p.id = :kycProfileId AND h.status <> :archived ORDER BY h.decisionAt DESC, h.id DESC",
                        KycDecisionHistory.class
                )
                .setParameter("kycProfileId", kycProfileId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getResultList();
    }

    public Long countActiveByKycProfileId(Long kycProfileId) {
        return entityManager.createQuery(
                        "SELECT COUNT(h.id) FROM KycDecisionHistory h " +
                                "WHERE h.kycProfile.id = :kycProfileId AND h.status <> :archived",
                        Long.class
                )
                .setParameter("kycProfileId", kycProfileId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }
}
