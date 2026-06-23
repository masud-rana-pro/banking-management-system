package com.sbms.security.repository;

import com.sbms.security.entity.AuditLog;
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
public class AuditLogRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public AuditLog save(AuditLog entity) {
        entityManager.persist(entity);
        return entity;
    }

    public Optional<AuditLog> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT a FROM AuditLog a WHERE a.id = :id",
                            AuditLog.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<AuditLog> findAll(String moduleName, String keyword) {
        StringBuilder hql = new StringBuilder("SELECT a FROM AuditLog a WHERE 1 = 1");
        if (moduleName != null && !moduleName.trim().isEmpty()) {
            hql.append(" AND UPPER(a.moduleName) = :moduleName");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("""
                     AND (
                        LOWER(a.moduleName) LIKE :keyword
                        OR LOWER(a.actionName) LIKE :keyword
                        OR LOWER(a.performedBy) LIKE :keyword
                     )
                    """);
        }
        hql.append(" ORDER BY a.performedAt DESC, a.id DESC");

        var query = entityManager.createQuery(hql.toString(), AuditLog.class);
        if (moduleName != null && !moduleName.trim().isEmpty()) {
            query.setParameter("moduleName", moduleName.trim().toUpperCase());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public List<AuditLog> findRelated(String moduleName, Long referenceId) {
        return entityManager.createQuery(
                        "SELECT a FROM AuditLog a WHERE UPPER(a.moduleName) = :moduleName AND a.referenceId = :referenceId ORDER BY a.performedAt DESC, a.id DESC",
                        AuditLog.class
                )
                .setParameter("moduleName", moduleName.trim().toUpperCase())
                .setParameter("referenceId", referenceId)
                .getResultList();
    }

    public Long countBetween(LocalDateTime fromTime, LocalDateTime toTime) {
        return entityManager.createQuery(
                        "SELECT COUNT(a.id) FROM AuditLog a WHERE a.performedAt BETWEEN :fromTime AND :toTime",
                        Long.class
                )
                .setParameter("fromTime", fromTime)
                .setParameter("toTime", toTime)
                .getSingleResult();
    }

    public List<AuditLog> findRecent(int limit) {
        return entityManager.createQuery(
                        "SELECT a FROM AuditLog a ORDER BY a.performedAt DESC, a.id DESC",
                        AuditLog.class
                )
                .setMaxResults(limit)
                .getResultList();
    }
}
