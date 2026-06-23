package com.sbms.security.repository;

import com.sbms.security.entity.SecurityEventLog;
import com.sbms.security.enums.SecuritySeverityLevel;
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
public class SecurityEventLogRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public SecurityEventLog save(SecurityEventLog entity) {
        entityManager.persist(entity);
        return entity;
    }

    public SecurityEventLog update(SecurityEventLog entity) {
        return entityManager.merge(entity);
    }

    public Optional<SecurityEventLog> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT e FROM SecurityEventLog e WHERE e.id = :id",
                            SecurityEventLog.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<SecurityEventLog> findAll(String severityLevel, String keyword) {
        StringBuilder hql = new StringBuilder("SELECT e FROM SecurityEventLog e WHERE 1 = 1");
        if (severityLevel != null && !severityLevel.trim().isEmpty()) {
            hql.append(" AND e.severityLevel = :severityLevel");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("""
                     AND (
                        LOWER(e.eventCode) LIKE :keyword
                        OR LOWER(e.eventName) LIKE :keyword
                        OR LOWER(COALESCE(e.referenceModule, '')) LIKE :keyword
                        OR LOWER(COALESCE(e.remarks, '')) LIKE :keyword
                     )
                    """);
        }
        hql.append(" ORDER BY e.eventTime DESC, e.id DESC");

        var query = entityManager.createQuery(hql.toString(), SecurityEventLog.class);
        if (severityLevel != null && !severityLevel.trim().isEmpty()) {
            query.setParameter("severityLevel", SecuritySeverityLevel.valueOf(severityLevel.trim().toUpperCase()));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public List<SecurityEventLog> findSuspiciousActivities(String keyword) {
        StringBuilder hql = new StringBuilder("""
                SELECT e
                FROM SecurityEventLog e
                WHERE (
                    e.severityLevel IN :severeLevels
                    OR UPPER(e.eventCode) IN :eventCodes
                )
                """);
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("""
                     AND (
                        LOWER(e.eventCode) LIKE :keyword
                        OR LOWER(e.eventName) LIKE :keyword
                        OR LOWER(COALESCE(e.referenceModule, '')) LIKE :keyword
                        OR LOWER(COALESCE(e.remarks, '')) LIKE :keyword
                     )
                    """);
        }
        hql.append(" ORDER BY e.eventTime DESC, e.id DESC");

        var query = entityManager.createQuery(hql.toString(), SecurityEventLog.class)
                .setParameter("severeLevels", List.of(SecuritySeverityLevel.HIGH, SecuritySeverityLevel.CRITICAL))
                .setParameter("eventCodes", List.of("FAILED_LOGIN", "SUSPICIOUS_TXN", "AML_FLAG", "SANCTION_HIT", "MULTIPLE_REVERSAL"));
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public Long countByEventCodeBetween(String eventCode, LocalDateTime fromTime, LocalDateTime toTime) {
        return entityManager.createQuery(
                        "SELECT COUNT(e.id) FROM SecurityEventLog e WHERE UPPER(e.eventCode) = :eventCode AND e.eventTime BETWEEN :fromTime AND :toTime",
                        Long.class
                )
                .setParameter("eventCode", eventCode.trim().toUpperCase())
                .setParameter("fromTime", fromTime)
                .setParameter("toTime", toTime)
                .getSingleResult();
    }

    public Long countAmlFlagsBetween(LocalDateTime fromTime, LocalDateTime toTime) {
        return entityManager.createQuery(
                        "SELECT COUNT(e.id) FROM SecurityEventLog e WHERE UPPER(e.eventCode) IN :eventCodes AND e.eventTime BETWEEN :fromTime AND :toTime",
                        Long.class
                )
                .setParameter("eventCodes", List.of("AML_FLAG", "SANCTION_HIT"))
                .setParameter("fromTime", fromTime)
                .setParameter("toTime", toTime)
                .getSingleResult();
    }

    public Long countSuspiciousActivities() {
        return entityManager.createQuery("""
                        SELECT COUNT(e.id)
                        FROM SecurityEventLog e
                        WHERE e.severityLevel IN :severeLevels
                           OR UPPER(e.eventCode) IN :eventCodes
                        """,
                        Long.class
                )
                .setParameter("severeLevels", List.of(SecuritySeverityLevel.HIGH, SecuritySeverityLevel.CRITICAL))
                .setParameter("eventCodes", List.of("FAILED_LOGIN", "SUSPICIOUS_TXN", "AML_FLAG", "SANCTION_HIT", "MULTIPLE_REVERSAL"))
                .getSingleResult();
    }

    public List<SecurityEventLog> findRecent(int limit) {
        return entityManager.createQuery(
                        "SELECT e FROM SecurityEventLog e ORDER BY e.eventTime DESC, e.id DESC",
                        SecurityEventLog.class
                )
                .setMaxResults(limit)
                .getResultList();
    }
}
