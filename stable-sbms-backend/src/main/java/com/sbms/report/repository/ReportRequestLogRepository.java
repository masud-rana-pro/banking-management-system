package com.sbms.report.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.report.entity.ReportRequestLog;
import com.sbms.report.enums.ReportRequestStatus;
import com.sbms.report.enums.ReportType;
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
public class ReportRequestLogRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ReportRequestLog save(ReportRequestLog entity) {
        entityManager.persist(entity);
        return entity;
    }

    public ReportRequestLog update(ReportRequestLog entity) {
        return entityManager.merge(entity);
    }

    public Optional<ReportRequestLog> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT l FROM ReportRequestLog l LEFT JOIN FETCH l.generatedFile WHERE l.id = :id",
                            ReportRequestLog.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<ReportRequestLog> findAll(String reportType, String requestStatus, String keyword) {
        StringBuilder hql = new StringBuilder("""
                SELECT l
                FROM ReportRequestLog l
                JOIN FETCH l.report r
                LEFT JOIN FETCH l.generatedFile gf
                WHERE 1 = 1
                """);

        if (reportType != null && !reportType.trim().isEmpty()) {
            hql.append(" AND r.reportType = :reportType");
        }
        if (requestStatus != null && !requestStatus.trim().isEmpty()) {
            hql.append(" AND l.requestStatus = :requestStatus");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("""
                     AND (
                        LOWER(r.reportCode) LIKE :keyword
                        OR LOWER(r.reportName) LIKE :keyword
                        OR LOWER(l.requestedBy) LIKE :keyword
                     )
                    """);
        }
        hql.append(" ORDER BY l.requestedAt DESC");

        var query = entityManager.createQuery(hql.toString(), ReportRequestLog.class);
        if (reportType != null && !reportType.trim().isEmpty()) {
            query.setParameter("reportType", ReportType.valueOf(reportType.trim().toUpperCase()));
        }
        if (requestStatus != null && !requestStatus.trim().isEmpty()) {
            query.setParameter("requestStatus", ReportRequestStatus.valueOf(requestStatus.trim().toUpperCase()));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public List<ReportRequestLog> findRecent(int limit) {
        return entityManager.createQuery(
                        "SELECT l FROM ReportRequestLog l JOIN FETCH l.report r LEFT JOIN FETCH l.generatedFile gf ORDER BY l.requestedAt DESC",
                        ReportRequestLog.class
                )
                .setMaxResults(limit)
                .getResultList();
    }

    public List<ReportRequestLog> findRecentByReportId(Long reportId, int limit) {
        return entityManager.createQuery(
                        "SELECT l FROM ReportRequestLog l JOIN FETCH l.report r LEFT JOIN FETCH l.generatedFile gf WHERE r.id = :reportId ORDER BY l.requestedAt DESC",
                        ReportRequestLog.class
                )
                .setParameter("reportId", reportId)
                .setMaxResults(limit)
                .getResultList();
    }

    public Long countGeneratedToday(LocalDateTime fromTime, LocalDateTime toTime) {
        return entityManager.createQuery(
                        "SELECT COUNT(l.id) FROM ReportRequestLog l WHERE l.generatedAt BETWEEN :fromTime AND :toTime",
                        Long.class
                )
                .setParameter("fromTime", fromTime)
                .setParameter("toTime", toTime)
                .getSingleResult();
    }

    public Long countByRequestStatus(ReportRequestStatus requestStatus) {
        return entityManager.createQuery(
                        "SELECT COUNT(l.id) FROM ReportRequestLog l WHERE l.requestStatus = :requestStatus AND l.status = :status",
                        Long.class
                )
                .setParameter("requestStatus", requestStatus)
                .setParameter("status", RecordStatus.ACTIVE)
                .getSingleResult();
    }

    public Long countRegulatoryPending() {
        return entityManager.createQuery("""
                        SELECT COUNT(l.id)
                        FROM ReportRequestLog l
                        JOIN l.report r
                        WHERE l.requestStatus = :requestStatus
                        AND l.status = :status
                        AND r.reportType IN :reportTypes
                        """,
                        Long.class
                )
                .setParameter("requestStatus", ReportRequestStatus.REQUESTED)
                .setParameter("status", RecordStatus.ACTIVE)
                .setParameter("reportTypes", List.of(ReportType.REGULATORY, ReportType.PAR, ReportType.SHARIAH_AUDIT))
                .getSingleResult();
    }

    public List<Object[]> findMostUsedReports(int limit) {
        return entityManager.createQuery("""
                        SELECT r.reportCode, r.reportName, r.queryKey, COUNT(l.id)
                        FROM ReportRequestLog l
                        JOIN l.report r
                        GROUP BY r.reportCode, r.reportName, r.queryKey
                        ORDER BY COUNT(l.id) DESC, r.reportName ASC
                        """,
                        Object[].class
                )
                .setMaxResults(limit)
                .getResultList();
    }
}
