package com.sbms.transaction.repository;

import com.sbms.transaction.entity.TransactionReversal;
import com.sbms.transaction.enums.ReversalStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class TransactionReversalRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public TransactionReversal save(TransactionReversal entity) {
        entityManager.persist(entity);
        return entity;
    }

    public TransactionReversal update(TransactionReversal entity) {
        return entityManager.merge(entity);
    }

    public Optional<TransactionReversal> findByOriginalTransactionId(Long originalTransactionId) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT r FROM TransactionReversal r " +
                                    "LEFT JOIN FETCH r.reversalTransaction " +
                                    "WHERE r.originalTransaction.id = :originalTransactionId " +
                                    "ORDER BY r.id DESC",
                            TransactionReversal.class
                    )
                    .setParameter("originalTransactionId", originalTransactionId)
                    .setMaxResults(1)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Long countByStatus(ReversalStatus status) {
        return entityManager.createQuery(
                        "SELECT COUNT(r.id) FROM TransactionReversal r WHERE r.status = :status",
                        Long.class
                )
                .setParameter("status", status)
                .getSingleResult();
    }
}
