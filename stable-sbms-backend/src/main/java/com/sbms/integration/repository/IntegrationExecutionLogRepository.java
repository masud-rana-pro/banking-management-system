package com.sbms.integration.repository;

import com.sbms.integration.entity.IntegrationExecutionLog;
import com.sbms.integration.enums.IntegrationExecutionStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class IntegrationExecutionLogRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public IntegrationExecutionLog save(IntegrationExecutionLog entity) {
        entityManager.persist(entity);
        return entity;
    }

    public IntegrationExecutionLog update(IntegrationExecutionLog entity) {
        return entityManager.merge(entity);
    }

    public Optional<IntegrationExecutionLog> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT l FROM IntegrationExecutionLog l " +
                            "JOIN FETCH l.provider p " +
                            "WHERE l.id = :id",
                    IntegrationExecutionLog.class
            ).setParameter("id", id).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<IntegrationExecutionLog> findAll(Long providerId, String executionStatus, String keyword) {
        StringBuilder hql = new StringBuilder(
                "SELECT l FROM IntegrationExecutionLog l " +
                        "JOIN FETCH l.provider p " +
                        "WHERE 1=1 "
        );
        if (providerId != null) hql.append("AND p.id = :providerId ");
        if (executionStatus != null && !executionStatus.trim().isEmpty()) hql.append("AND l.executionStatus = :executionStatus ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("AND (LOWER(p.providerCode) LIKE :keyword OR LOWER(p.providerName) LIKE :keyword OR LOWER(COALESCE(l.referenceModule, '')) LIKE :keyword OR LOWER(COALESCE(l.requestPayload, '')) LIKE :keyword OR LOWER(COALESCE(l.responsePayload, '')) LIKE :keyword) ");
        }
        hql.append("ORDER BY l.id DESC");

        TypedQuery<IntegrationExecutionLog> query = entityManager.createQuery(hql.toString(), IntegrationExecutionLog.class);
        if (providerId != null) query.setParameter("providerId", providerId);
        if (executionStatus != null && !executionStatus.trim().isEmpty()) {
            query.setParameter("executionStatus", IntegrationExecutionStatus.valueOf(executionStatus.trim().toUpperCase()));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public List<IntegrationExecutionLog> findRecent(int limit) {
        return entityManager.createQuery(
                "SELECT l FROM IntegrationExecutionLog l " +
                        "JOIN FETCH l.provider p " +
                        "ORDER BY l.id DESC",
                IntegrationExecutionLog.class
        ).setMaxResults(limit).getResultList();
    }

    public Optional<IntegrationExecutionLog> findLatestByProviderId(Long providerId) {
        List<IntegrationExecutionLog> result = entityManager.createQuery(
                "SELECT l FROM IntegrationExecutionLog l " +
                        "JOIN FETCH l.provider p " +
                        "WHERE p.id = :providerId ORDER BY l.id DESC",
                IntegrationExecutionLog.class
        ).setParameter("providerId", providerId).setMaxResults(1).getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public long countByProviderId(Long providerId) {
        return entityManager.createQuery(
                "SELECT COUNT(l.id) FROM IntegrationExecutionLog l WHERE l.provider.id = :providerId",
                Long.class
        ).setParameter("providerId", providerId).getSingleResult();
    }

    public long countFailuresByProviderId(Long providerId) {
        return entityManager.createQuery(
                "SELECT COUNT(l.id) FROM IntegrationExecutionLog l WHERE l.provider.id = :providerId AND l.executionStatus = :status",
                Long.class
        ).setParameter("providerId", providerId)
                .setParameter("status", IntegrationExecutionStatus.FAILED)
                .getSingleResult();
    }

    public long countByStatus(IntegrationExecutionStatus executionStatus) {
        return entityManager.createQuery(
                "SELECT COUNT(l.id) FROM IntegrationExecutionLog l WHERE l.executionStatus = :status",
                Long.class
        ).setParameter("status", executionStatus).getSingleResult();
    }

    public long countTotal() {
        return entityManager.createQuery(
                "SELECT COUNT(l.id) FROM IntegrationExecutionLog l",
                Long.class
        ).getSingleResult();
    }

    public LocalDateTime findLastSuccessfulSync() {
        try {
            return entityManager.createQuery(
                    "SELECT MAX(l.executedAt) FROM IntegrationExecutionLog l WHERE l.executionStatus = :status",
                    LocalDateTime.class
            ).setParameter("status", IntegrationExecutionStatus.SUCCESS).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
