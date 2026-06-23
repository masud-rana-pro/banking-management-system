package com.sbms.financing.repository;

import com.sbms.financing.entity.FinancingAssetVerification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class FinancingAssetVerificationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public FinancingAssetVerification save(FinancingAssetVerification entity) {
        entityManager.persist(entity);
        return entity;
    }

    public FinancingAssetVerification update(FinancingAssetVerification entity) {
        return entityManager.merge(entity);
    }

    public Optional<FinancingAssetVerification> findByApplicationId(Long applicationId) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT v FROM FinancingAssetVerification v " +
                                    "JOIN FETCH v.application a " +
                                    "WHERE a.id = :applicationId",
                            FinancingAssetVerification.class
                    ).setParameter("applicationId", applicationId).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
