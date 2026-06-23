package com.sbms.profit.repository;

import com.sbms.profit.entity.BalanceSnapshot;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Repository
@Transactional
public class BalanceSnapshotRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public BalanceSnapshot save(BalanceSnapshot entity) {
        entityManager.persist(entity);
        return entity;
    }

    public BalanceSnapshot update(BalanceSnapshot entity) {
        return entityManager.merge(entity);
    }

    public Optional<BalanceSnapshot> findByAccountIdAndDate(Long accountId, LocalDate snapshotDate) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT b FROM BalanceSnapshot b " +
                                    "JOIN FETCH b.account a " +
                                    "WHERE a.id = :accountId AND b.snapshotDate = :snapshotDate",
                            BalanceSnapshot.class
                    )
                    .setParameter("accountId", accountId)
                    .setParameter("snapshotDate", snapshotDate)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
