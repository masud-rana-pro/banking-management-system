package com.sbms.depositscheme.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.depositscheme.entity.DepositScheme;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class DepositSchemeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public DepositScheme save(DepositScheme entity) {
        entityManager.persist(entity);
        return entity;
    }

    public DepositScheme update(DepositScheme entity) {
        return entityManager.merge(entity);
    }

    public Optional<DepositScheme> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT s FROM DepositScheme s WHERE s.id = :id",
                            DepositScheme.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<DepositScheme> findAll() {
        return entityManager.createQuery(
                        "SELECT s FROM DepositScheme s ORDER BY s.id DESC",
                        DepositScheme.class
                )
                .getResultList();
    }

    public List<DepositScheme> findLatest(int limit) {
        return entityManager.createQuery(
                        "SELECT s FROM DepositScheme s ORDER BY s.id DESC",
                        DepositScheme.class
                )
                .setMaxResults(limit)
                .getResultList();
    }

    public Optional<DepositScheme> findBySchemeCode(String schemeCode) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT s FROM DepositScheme s WHERE LOWER(s.schemeCode) = :schemeCode",
                            DepositScheme.class
                    )
                    .setParameter("schemeCode", schemeCode.toLowerCase())
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Long countTotalSchemes() {
        return entityManager.createQuery(
                        "SELECT COUNT(s.id) FROM DepositScheme s WHERE s.status <> :archived",
                        Long.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public String findLastSchemeCode() {
        List<String> result = entityManager.createQuery(
                        "SELECT s.schemeCode FROM DepositScheme s WHERE s.schemeCode LIKE :prefix ORDER BY s.schemeCode DESC",
                        String.class
                )
                .setParameter("prefix", "DPS-%")
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }
}
