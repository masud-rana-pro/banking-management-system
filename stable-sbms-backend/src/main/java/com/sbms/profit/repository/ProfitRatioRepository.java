package com.sbms.profit.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.profit.entity.ProfitRatio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ProfitRatioRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ProfitRatio save(ProfitRatio entity) {
        entityManager.persist(entity);
        return entity;
    }

    public ProfitRatio update(ProfitRatio entity) {
        return entityManager.merge(entity);
    }

    public Optional<ProfitRatio> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT p FROM ProfitRatio p " +
                                    "JOIN FETCH p.accountType at " +
                                    "WHERE p.id = :id",
                            ProfitRatio.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<ProfitRatio> findAll() {
        return entityManager.createQuery(
                        "SELECT p FROM ProfitRatio p " +
                                "JOIN FETCH p.accountType at " +
                                "ORDER BY p.id DESC",
                        ProfitRatio.class
                )
                .getResultList();
    }

    public List<ProfitRatio> findDropdown() {
        return entityManager.createQuery(
                        "SELECT p FROM ProfitRatio p " +
                                "JOIN FETCH p.accountType at " +
                                "WHERE p.status = :active " +
                                "ORDER BY p.effectiveFrom DESC, p.id DESC",
                        ProfitRatio.class
                )
                .setParameter("active", RecordStatus.ACTIVE)
                .getResultList();
    }

    public Optional<ProfitRatio> findByRatioCode(String ratioCode) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT p FROM ProfitRatio p " +
                                    "JOIN FETCH p.accountType at " +
                                    "WHERE LOWER(p.ratioCode) = :ratioCode",
                            ProfitRatio.class
                    )
                    .setParameter("ratioCode", ratioCode.toLowerCase())
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public boolean existsOverlap(Long accountTypeId, LocalDate effectiveFrom, LocalDate effectiveTo, Long excludeId) {
        String hql = "SELECT COUNT(p.id) FROM ProfitRatio p " +
                "WHERE p.accountType.id = :accountTypeId " +
                "AND p.status <> :archived " +
                "AND (:excludeId IS NULL OR p.id <> :excludeId) " +
                "AND p.effectiveFrom <= :newTo " +
                "AND COALESCE(p.effectiveTo, :maxDate) >= :newFrom";

        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("accountTypeId", accountTypeId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("excludeId", excludeId)
                .setParameter("newTo", effectiveTo == null ? LocalDate.of(2999, 12, 31) : effectiveTo)
                .setParameter("maxDate", LocalDate.of(2999, 12, 31))
                .setParameter("newFrom", effectiveFrom)
                .getSingleResult();
        return count != null && count > 0;
    }

    public String findLastRatioCode() {
        List<String> refs = entityManager.createQuery(
                        "SELECT p.ratioCode FROM ProfitRatio p " +
                                "WHERE p.ratioCode LIKE :prefix " +
                                "ORDER BY p.ratioCode DESC",
                        String.class
                )
                .setParameter("prefix", "PSR-%")
                .setMaxResults(1)
                .getResultList();
        return refs.isEmpty() ? null : refs.get(0);
    }

    public Optional<ProfitRatio> findActiveRatio(Long accountTypeId, LocalDate postingDate) {
        List<ProfitRatio> result = entityManager.createQuery(
                        "SELECT p FROM ProfitRatio p " +
                                "JOIN FETCH p.accountType at " +
                                "WHERE at.id = :accountTypeId " +
                                "AND p.status = :active " +
                                "AND p.effectiveFrom <= :postingDate " +
                                "AND (p.effectiveTo IS NULL OR p.effectiveTo >= :postingDate) " +
                                "ORDER BY p.effectiveFrom DESC, p.id DESC",
                        ProfitRatio.class
                )
                .setParameter("accountTypeId", accountTypeId)
                .setParameter("active", RecordStatus.ACTIVE)
                .setParameter("postingDate", postingDate)
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public Long countActive(LocalDate currentDate) {
        return entityManager.createQuery(
                        "SELECT COUNT(p.id) FROM ProfitRatio p " +
                                "WHERE p.status = :active " +
                                "AND p.effectiveFrom <= :currentDate " +
                                "AND (p.effectiveTo IS NULL OR p.effectiveTo >= :currentDate)",
                        Long.class
                )
                .setParameter("active", RecordStatus.ACTIVE)
                .setParameter("currentDate", currentDate)
                .getSingleResult();
    }
}
