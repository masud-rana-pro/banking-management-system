package com.sbms.financing.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.financing.entity.FinancingDisbursement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
@Transactional
public class FinancingDisbursementRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public FinancingDisbursement save(FinancingDisbursement entity) {
        entityManager.persist(entity);
        return entity;
    }

    public FinancingDisbursement update(FinancingDisbursement entity) {
        return entityManager.merge(entity);
    }

    public Optional<FinancingDisbursement> findByApplicationId(Long applicationId) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT d FROM FinancingDisbursement d " +
                                    "JOIN FETCH d.application a " +
                                    "WHERE a.id = :applicationId",
                            FinancingDisbursement.class
                    ).setParameter("applicationId", applicationId).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public String findLastDisbursementNo() {
        var result = entityManager.createQuery(
                        "SELECT d.disbursementNo FROM FinancingDisbursement d " +
                                "WHERE d.disbursementNo LIKE :prefix ORDER BY d.disbursementNo DESC",
                        String.class
                ).setParameter("prefix", "FND-%").setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public BigDecimal sumDisbursedAmount() {
        BigDecimal result = entityManager.createQuery(
                        "SELECT COALESCE(SUM(d.disbursedAmount), 0) FROM FinancingDisbursement d WHERE d.status <> :archived",
                        BigDecimal.class
                ).setParameter("archived", RecordStatus.ARCHIVED).getSingleResult();
        return result == null ? BigDecimal.ZERO : result;
    }
}
