package com.sbms.statement.repository;

import com.sbms.statement.entity.BranchStatementRequest;
import com.sbms.statement.enums.StatementRequestStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class BranchStatementRequestRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public BranchStatementRequest save(BranchStatementRequest entity) {
        entityManager.persist(entity);
        return entity;
    }

    public BranchStatementRequest update(BranchStatementRequest entity) {
        return entityManager.merge(entity);
    }

    public Optional<BranchStatementRequest> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT r FROM BranchStatementRequest r " +
                                    "JOIN FETCH r.branch b " +
                                    "LEFT JOIN FETCH r.generatedFile f " +
                                    "WHERE r.id = :id",
                            BranchStatementRequest.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<BranchStatementRequest> findAll() {
        return entityManager.createQuery(
                        "SELECT r FROM BranchStatementRequest r " +
                                "JOIN FETCH r.branch b " +
                                "LEFT JOIN FETCH r.generatedFile f " +
                                "ORDER BY r.id DESC",
                        BranchStatementRequest.class
                )
                .getResultList();
    }

    public String findLastRequestNo() {
        List<String> refs = entityManager.createQuery(
                        "SELECT r.requestNo FROM BranchStatementRequest r " +
                                "WHERE r.requestNo LIKE :prefix " +
                                "ORDER BY r.requestNo DESC",
                        String.class
                )
                .setParameter("prefix", "BSR-%")
                .setMaxResults(1)
                .getResultList();
        return refs.isEmpty() ? null : refs.get(0);
    }

    public Long countAll() {
        return entityManager.createQuery(
                        "SELECT COUNT(r.id) FROM BranchStatementRequest r",
                        Long.class
                )
                .getSingleResult();
    }

    public Long countByStatus(StatementRequestStatus status) {
        return entityManager.createQuery(
                        "SELECT COUNT(r.id) FROM BranchStatementRequest r WHERE r.requestStatus = :status",
                        Long.class
                )
                .setParameter("status", status)
                .getSingleResult();
    }

    public Long countRequestedBetween(LocalDateTime fromTime, LocalDateTime toTime) {
        return entityManager.createQuery(
                        "SELECT COUNT(r.id) FROM BranchStatementRequest r WHERE r.requestedAt BETWEEN :fromTime AND :toTime",
                        Long.class
                )
                .setParameter("fromTime", fromTime)
                .setParameter("toTime", toTime)
                .getSingleResult();
    }

    public List<BranchStatementRequest> findRecent(int limit) {
        return entityManager.createQuery(
                        "SELECT r FROM BranchStatementRequest r " +
                                "JOIN FETCH r.branch b " +
                                "LEFT JOIN FETCH r.generatedFile f " +
                                "ORDER BY r.id DESC",
                        BranchStatementRequest.class
                )
                .setMaxResults(limit)
                .getResultList();
    }
}
