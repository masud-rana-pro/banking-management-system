package com.sbms.transaction.repository;

import com.sbms.transaction.entity.CashTransaction;
import com.sbms.transaction.enums.CashDirection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional
public class CashTransactionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public CashTransaction save(CashTransaction entity) {
        entityManager.persist(entity);
        return entity;
    }

    public Optional<CashTransaction> findByTransactionId(Long transactionId) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT c FROM CashTransaction c WHERE c.transaction.id = :transactionId",
                            CashTransaction.class
                    )
                    .setParameter("transactionId", transactionId)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public BigDecimal sumByTellerAndDirection(Long tellerUserId, CashDirection direction, LocalDateTime start, LocalDateTime end) {
        BigDecimal sum = entityManager.createQuery(
                        "SELECT COALESCE(SUM(c.amount), 0) FROM CashTransaction c " +
                                "WHERE c.tellerUserId = :tellerUserId " +
                                "AND c.cashDirection = :direction " +
                                "AND c.createdAt >= :start AND c.createdAt < :end",
                        BigDecimal.class
                )
                .setParameter("tellerUserId", tellerUserId)
                .setParameter("direction", direction)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
        return sum == null ? BigDecimal.ZERO : sum;
    }

    public BigDecimal sumByDateRange(LocalDateTime start, LocalDateTime end) {
        BigDecimal sum = entityManager.createQuery(
                        "SELECT COALESCE(SUM(c.amount), 0) FROM CashTransaction c " +
                                "WHERE c.createdAt >= :start AND c.createdAt < :end",
                        BigDecimal.class
                )
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
        return sum == null ? BigDecimal.ZERO : sum;
    }
}
