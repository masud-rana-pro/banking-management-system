package com.sbms.transaction.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.transaction.entity.TransactionJournal;
import com.sbms.transaction.enums.TransactionStatus;
import com.sbms.transaction.enums.TransactionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class TransactionJournalRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public TransactionJournal save(TransactionJournal entity) {
        entityManager.persist(entity);
        return entity;
    }

    public TransactionJournal update(TransactionJournal entity) {
        return entityManager.merge(entity);
    }

    public Optional<TransactionJournal> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT t FROM TransactionJournal t " +
                                    "LEFT JOIN FETCH t.debitAccount da " +
                                    "LEFT JOIN FETCH da.customer " +
                                    "LEFT JOIN FETCH t.creditAccount ca " +
                                    "LEFT JOIN FETCH ca.customer " +
                                    "LEFT JOIN FETCH t.parentTransaction pt " +
                                    "WHERE t.id = :id",
                            TransactionJournal.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<TransactionJournal> findAll() {
        return entityManager.createQuery(
                        "SELECT t FROM TransactionJournal t " +
                                "LEFT JOIN FETCH t.debitAccount da " +
                                "LEFT JOIN FETCH da.customer " +
                                "LEFT JOIN FETCH t.creditAccount ca " +
                                "LEFT JOIN FETCH ca.customer " +
                                "LEFT JOIN FETCH t.parentTransaction pt " +
                                "WHERE t.status <> :archived " +
                                "ORDER BY t.id DESC",
                        TransactionJournal.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getResultList();
    }

    public List<TransactionJournal> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
        return entityManager.createQuery(
                        "SELECT t FROM TransactionJournal t " +
                                "LEFT JOIN FETCH t.debitAccount da " +
                                "LEFT JOIN FETCH da.customer " +
                                "LEFT JOIN FETCH t.creditAccount ca " +
                                "LEFT JOIN FETCH ca.customer " +
                                "LEFT JOIN FETCH t.parentTransaction pt " +
                                "WHERE t.status <> :archived AND (" +
                                "LOWER(t.transactionRef) LIKE :keyword OR " +
                                "LOWER(COALESCE(t.narration, '')) LIKE :keyword OR " +
                                "LOWER(COALESCE(da.accountNumber, '')) LIKE :keyword OR " +
                                "LOWER(COALESCE(ca.accountNumber, '')) LIKE :keyword OR " +
                                "LOWER(COALESCE(da.customer.fullName, '')) LIKE :keyword OR " +
                                "LOWER(COALESCE(ca.customer.fullName, '')) LIKE :keyword" +
                                ") ORDER BY t.id DESC",
                        TransactionJournal.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("keyword", likeKeyword)
                .getResultList();
    }

    public Optional<TransactionJournal> findByTransactionRef(String transactionRef) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT t FROM TransactionJournal t WHERE t.transactionRef = :transactionRef",
                            TransactionJournal.class
                    )
                    .setParameter("transactionRef", transactionRef)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public String findLastTransactionRef() {
        List<String> refs = entityManager.createQuery(
                        "SELECT t.transactionRef FROM TransactionJournal t " +
                                "WHERE t.transactionRef LIKE :prefix ORDER BY t.transactionRef DESC",
                        String.class
                )
                .setParameter("prefix", "TXN-%")
                .setMaxResults(1)
                .getResultList();
        return refs.isEmpty() ? null : refs.get(0);
    }

    public BigDecimal sumByTypeAndDateRange(TransactionType transactionType, LocalDateTime start, LocalDateTime end) {
        BigDecimal sum = entityManager.createQuery(
                        "SELECT COALESCE(SUM(t.amount), 0) FROM TransactionJournal t " +
                                "WHERE t.transactionType = :transactionType " +
                                "AND t.transactionDate >= :start AND t.transactionDate < :end " +
                                "AND t.status <> :archived AND t.transactionStatus = :posted",
                        BigDecimal.class
                )
                .setParameter("transactionType", transactionType)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("posted", TransactionStatus.POSTED)
                .getSingleResult();
        return sum == null ? BigDecimal.ZERO : sum;
    }

    public Long countSuspicious(BigDecimal threshold, LocalDateTime start, LocalDateTime end) {
        return entityManager.createQuery(
                        "SELECT COUNT(t.id) FROM TransactionJournal t " +
                                "WHERE t.amount >= :threshold " +
                                "AND t.transactionDate >= :start AND t.transactionDate < :end " +
                                "AND t.status <> :archived",
                        Long.class
                )
                .setParameter("threshold", threshold)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public List<Object[]> topBranches(LocalDateTime start, LocalDateTime end) {
        return entityManager.createQuery(
                        "SELECT t.branchId, COUNT(t.id) FROM TransactionJournal t " +
                                "WHERE t.transactionDate >= :start AND t.transactionDate < :end " +
                                "AND t.status <> :archived " +
                                "GROUP BY t.branchId ORDER BY COUNT(t.id) DESC",
                        Object[].class
                )
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .setMaxResults(5)
                .getResultList();
    }
}
