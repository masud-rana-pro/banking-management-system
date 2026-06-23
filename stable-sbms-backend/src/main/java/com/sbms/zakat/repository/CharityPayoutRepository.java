package com.sbms.zakat.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.zakat.entity.CharityPayout;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CharityPayoutRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public CharityPayout save(CharityPayout entity) {
        entityManager.persist(entity);
        return entity;
    }

    public List<CharityPayout> findAll() {
        return entityManager.createQuery(
                "SELECT p FROM CharityPayout p JOIN FETCH p.beneficiary b ORDER BY p.id DESC",
                CharityPayout.class
        ).getResultList();
    }

    public List<CharityPayout> findLatest(int limit) {
        return entityManager.createQuery(
                "SELECT p FROM CharityPayout p JOIN FETCH p.beneficiary b ORDER BY p.id DESC",
                CharityPayout.class
        ).setMaxResults(limit).getResultList();
    }

    public Optional<CharityPayout> findById(Long id) {
        List<CharityPayout> results = entityManager.createQuery(
                "SELECT p FROM CharityPayout p JOIN FETCH p.beneficiary b WHERE p.id = :id",
                CharityPayout.class
        ).setParameter("id", id).setMaxResults(1).getResultList();
        return results.stream().findFirst();
    }

    public Long countByBeneficiaryId(Long beneficiaryId) {
        return entityManager.createQuery(
                "SELECT COUNT(p.id) FROM CharityPayout p WHERE p.beneficiary.id = :beneficiaryId",
                Long.class
        ).setParameter("beneficiaryId", beneficiaryId).getSingleResult();
    }

    public BigDecimal sumPayoutAmount() {
        BigDecimal value = entityManager.createQuery(
                "SELECT COALESCE(SUM(p.amount), 0) FROM CharityPayout p WHERE p.status <> :archived",
                BigDecimal.class
        ).setParameter("archived", RecordStatus.ARCHIVED).getSingleResult();
        return value == null ? BigDecimal.ZERO : value;
    }
}
