package com.sbms.statement.repository;

import com.sbms.statement.entity.CustomerStatementRequest;
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
public class CustomerStatementRequestRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public CustomerStatementRequest save(CustomerStatementRequest entity) {
        entityManager.persist(entity);
        return entity;
    }

    public CustomerStatementRequest update(CustomerStatementRequest entity) {
        return entityManager.merge(entity);
    }

    public Optional<CustomerStatementRequest> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT r FROM CustomerStatementRequest r " +
                                    "JOIN FETCH r.customer c " +
                                    "JOIN FETCH r.account a " +
                                    "LEFT JOIN FETCH r.generatedFile f " +
                                    "WHERE r.id = :id",
                            CustomerStatementRequest.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<CustomerStatementRequest> findAll() {
        return entityManager.createQuery(
                        "SELECT r FROM CustomerStatementRequest r " +
                                "JOIN FETCH r.customer c " +
                                "JOIN FETCH r.account a " +
                                "LEFT JOIN FETCH r.generatedFile f " +
                                "ORDER BY r.id DESC",
                        CustomerStatementRequest.class
                )
                .getResultList();
    }

    public String findLastRequestNo() {
        List<String> refs = entityManager.createQuery(
                        "SELECT r.requestNo FROM CustomerStatementRequest r " +
                                "WHERE r.requestNo LIKE :prefix " +
                                "ORDER BY r.requestNo DESC",
                        String.class
                )
                .setParameter("prefix", "CSR-%")
                .setMaxResults(1)
                .getResultList();
        return refs.isEmpty() ? null : refs.get(0);
    }

    public Long countAll() {
        return entityManager.createQuery(
                        "SELECT COUNT(r.id) FROM CustomerStatementRequest r",
                        Long.class
                )
                .getSingleResult();
    }

    public Long countByStatus(StatementRequestStatus status) {
        return entityManager.createQuery(
                        "SELECT COUNT(r.id) FROM CustomerStatementRequest r WHERE r.requestStatus = :status",
                        Long.class
                )
                .setParameter("status", status)
                .getSingleResult();
    }

    public Long countRequestedBetween(LocalDateTime fromTime, LocalDateTime toTime) {
        return entityManager.createQuery(
                        "SELECT COUNT(r.id) FROM CustomerStatementRequest r WHERE r.requestedAt BETWEEN :fromTime AND :toTime",
                        Long.class
                )
                .setParameter("fromTime", fromTime)
                .setParameter("toTime", toTime)
                .getSingleResult();
    }

    public List<CustomerStatementRequest> findRecent(int limit) {
        return entityManager.createQuery(
                        "SELECT r FROM CustomerStatementRequest r " +
                                "JOIN FETCH r.customer c " +
                                "JOIN FETCH r.account a " +
                                "LEFT JOIN FETCH r.generatedFile f " +
                                "ORDER BY r.id DESC",
                        CustomerStatementRequest.class
                )
                .setMaxResults(limit)
                .getResultList();
    }
}
