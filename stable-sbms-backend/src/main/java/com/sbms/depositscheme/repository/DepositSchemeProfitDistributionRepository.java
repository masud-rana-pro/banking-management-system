package com.sbms.depositscheme.repository;

import com.sbms.depositscheme.entity.DepositSchemeProfitDistribution;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class DepositSchemeProfitDistributionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void saveAll(List<DepositSchemeProfitDistribution> entities) {
        for (int i = 0; i < entities.size(); i++) {
            entityManager.persist(entities.get(i));
            if ((i + 1) % 20 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    public List<DepositSchemeProfitDistribution> findByEnrollmentId(Long enrollmentId) {
        return entityManager.createQuery(
                        "SELECT p FROM DepositSchemeProfitDistribution p " +
                                "JOIN FETCH p.enrollment e " +
                                "WHERE e.id = :enrollmentId ORDER BY p.distributionNo ASC",
                        DepositSchemeProfitDistribution.class
                )
                .setParameter("enrollmentId", enrollmentId)
                .getResultList();
    }
}
