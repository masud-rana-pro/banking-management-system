package com.sbms.zakat.repository;

import com.sbms.zakat.entity.CharityFund;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional
public class CharityFundRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public CharityFund save(CharityFund entity) {
        entityManager.persist(entity);
        return entity;
    }

    public List<CharityFund> findAll() {
        return entityManager.createQuery(
                "SELECT f FROM CharityFund f ORDER BY f.id DESC",
                CharityFund.class
        ).getResultList();
    }

    public List<CharityFund> findLatest(int limit) {
        return entityManager.createQuery(
                "SELECT f FROM CharityFund f ORDER BY f.id DESC",
                CharityFund.class
        ).setMaxResults(limit).getResultList();
    }

    public BigDecimal currentBalance() {
        List<BigDecimal> rows = entityManager.createQuery(
                "SELECT f.balanceAfter FROM CharityFund f ORDER BY f.id DESC",
                BigDecimal.class
        ).setMaxResults(1).getResultList();
        return rows.isEmpty() || rows.get(0) == null ? BigDecimal.ZERO : rows.get(0);
    }
}
