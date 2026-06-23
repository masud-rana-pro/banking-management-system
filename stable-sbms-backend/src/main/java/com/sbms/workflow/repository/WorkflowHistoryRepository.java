package com.sbms.workflow.repository;

import com.sbms.workflow.entity.WorkflowHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class WorkflowHistoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public WorkflowHistory save(WorkflowHistory entity) {
        entityManager.persist(entity);
        return entity;
    }

    public WorkflowHistory update(WorkflowHistory entity) {
        return entityManager.merge(entity);
    }

    public Optional<WorkflowHistory> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT h FROM WorkflowHistory h WHERE h.id = :id",
                            WorkflowHistory.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<WorkflowHistory> findAll(String moduleName, String keyword) {
        StringBuilder hql = new StringBuilder("SELECT h FROM WorkflowHistory h WHERE 1 = 1");
        if (moduleName != null && !moduleName.trim().isEmpty()) {
            hql.append(" AND UPPER(h.moduleName) = :moduleName");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("""
                     AND (
                        LOWER(h.moduleName) LIKE :keyword
                        OR LOWER(h.actionName) LIKE :keyword
                        OR LOWER(h.actionBy) LIKE :keyword
                        OR LOWER(COALESCE(h.remarks, '')) LIKE :keyword
                     )
                    """);
        }
        hql.append(" ORDER BY h.actionAt DESC, h.id DESC");
        var query = entityManager.createQuery(hql.toString(), WorkflowHistory.class);
        if (moduleName != null && !moduleName.trim().isEmpty()) {
            query.setParameter("moduleName", moduleName.trim().toUpperCase());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public List<WorkflowHistory> findPending(String keyword) {
        StringBuilder hql = new StringBuilder("""
                SELECT h FROM WorkflowHistory h
                WHERE UPPER(COALESCE(h.toStatus, '')) IN :pendingStatuses
                """);
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("""
                     AND (
                        LOWER(h.moduleName) LIKE :keyword
                        OR LOWER(h.actionBy) LIKE :keyword
                        OR LOWER(COALESCE(h.remarks, '')) LIKE :keyword
                     )
                    """);
        }
        hql.append(" ORDER BY h.actionAt DESC, h.id DESC");
        var query = entityManager.createQuery(hql.toString(), WorkflowHistory.class)
                .setParameter("pendingStatuses", List.of("PENDING", "SUBMITTED", "PENDING_REVIEW", "UNDER_REVIEW", "RETURNED", "ASSIGNED"));
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public List<WorkflowHistory> findByActionBy(String actionBy) {
        return entityManager.createQuery(
                        "SELECT h FROM WorkflowHistory h WHERE LOWER(h.actionBy) = :actionBy ORDER BY h.actionAt DESC, h.id DESC",
                        WorkflowHistory.class
                )
                .setParameter("actionBy", actionBy == null ? "" : actionBy.trim().toLowerCase())
                .getResultList();
    }

    public Long countPending() {
        return entityManager.createQuery(
                        "SELECT COUNT(h.id) FROM WorkflowHistory h WHERE UPPER(COALESCE(h.toStatus, '')) IN :pendingStatuses",
                        Long.class
                )
                .setParameter("pendingStatuses", List.of("PENDING", "SUBMITTED", "PENDING_REVIEW", "UNDER_REVIEW", "RETURNED", "ASSIGNED"))
                .getSingleResult();
    }

    public Long countCompleted() {
        return entityManager.createQuery(
                        "SELECT COUNT(h.id) FROM WorkflowHistory h WHERE UPPER(COALESCE(h.toStatus, '')) IN :completedStatuses",
                        Long.class
                )
                .setParameter("completedStatuses", List.of("APPROVED", "CLOSED", "COMPLETED", "VERIFIED", "POSTED"))
                .getSingleResult();
    }

    public Long countBottlenecks() {
        return entityManager.createQuery(
                        "SELECT COUNT(h.id) FROM WorkflowHistory h WHERE UPPER(COALESCE(h.toStatus, '')) IN :bottleneckStatuses",
                        Long.class
                )
                .setParameter("bottleneckStatuses", List.of("RETURNED", "FAILED", "REJECTED"))
                .getSingleResult();
    }

    public List<WorkflowHistory> findRecent(int limit) {
        return entityManager.createQuery(
                        "SELECT h FROM WorkflowHistory h ORDER BY h.actionAt DESC, h.id DESC",
                        WorkflowHistory.class
                )
                .setMaxResults(limit)
                .getResultList();
    }
}
