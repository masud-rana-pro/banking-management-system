package com.sbms.shariah.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.shariah.dto.response.ShariahModuleSummaryResponse;
import com.sbms.shariah.entity.ShariahReviewCase;
import com.sbms.shariah.enums.ShariahCaseStatus;
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
public class ShariahReviewCaseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ShariahReviewCase save(ShariahReviewCase entity) {
        entityManager.persist(entity);
        return entity;
    }

    public ShariahReviewCase update(ShariahReviewCase entity) {
        return entityManager.merge(entity);
    }

    public Optional<ShariahReviewCase> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT c FROM ShariahReviewCase c WHERE c.id = :id",
                    ShariahReviewCase.class
            ).setParameter("id", id).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<ShariahReviewCase> findAll(String referenceModule, ShariahCaseStatus caseStatus, String keyword) {
        StringBuilder hql = new StringBuilder("SELECT c FROM ShariahReviewCase c WHERE c.status <> :archived ");
        if (referenceModule != null && !referenceModule.trim().isEmpty()) {
            hql.append("AND UPPER(c.referenceModule) = :referenceModule ");
        }
        if (caseStatus != null) {
            hql.append("AND c.caseStatus = :caseStatus ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("AND (LOWER(c.caseNo) LIKE :keyword OR LOWER(c.submittedBy) LIKE :keyword OR LOWER(c.referenceModule) LIKE :keyword OR LOWER(COALESCE(c.remarks, '')) LIKE :keyword) ");
        }
        hql.append("ORDER BY c.id DESC");

        TypedQuery<ShariahReviewCase> query = entityManager.createQuery(hql.toString(), ShariahReviewCase.class)
                .setParameter("archived", RecordStatus.ARCHIVED);
        if (referenceModule != null && !referenceModule.trim().isEmpty()) {
            query.setParameter("referenceModule", referenceModule.trim().toUpperCase());
        }
        if (caseStatus != null) {
            query.setParameter("caseStatus", caseStatus);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public List<ShariahReviewCase> findLatest(int limit) {
        return entityManager.createQuery(
                "SELECT c FROM ShariahReviewCase c WHERE c.status <> :archived ORDER BY c.id DESC",
                ShariahReviewCase.class
        ).setParameter("archived", RecordStatus.ARCHIVED)
                .setMaxResults(limit)
                .getResultList();
    }

    public Optional<ShariahReviewCase> findOpenByReference(String referenceModule, Long referenceId) {
        List<ShariahReviewCase> result = entityManager.createQuery(
                "SELECT c FROM ShariahReviewCase c " +
                        "WHERE c.status <> :archived " +
                        "AND UPPER(c.referenceModule) = :referenceModule " +
                        "AND c.referenceId = :referenceId " +
                        "AND c.caseStatus IN :statuses " +
                        "ORDER BY c.id DESC",
                ShariahReviewCase.class
        ).setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("referenceModule", referenceModule.trim().toUpperCase())
                .setParameter("referenceId", referenceId)
                .setParameter("statuses", List.of(ShariahCaseStatus.PENDING_REVIEW, ShariahCaseStatus.RETURNED))
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public String findLastCaseNo() {
        List<String> result = entityManager.createQuery(
                "SELECT c.caseNo FROM ShariahReviewCase c WHERE c.caseNo LIKE :prefix ORDER BY c.caseNo DESC",
                String.class
        ).setParameter("prefix", "SHR-%")
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public Long countByStatus(ShariahCaseStatus status) {
        return entityManager.createQuery(
                "SELECT COUNT(c.id) FROM ShariahReviewCase c WHERE c.status <> :archived AND c.caseStatus = :status",
                Long.class
        ).setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("status", status)
                .getSingleResult();
    }

    public Long countUpcomingReviews() {
        return entityManager.createQuery(
                "SELECT COUNT(c.id) FROM ShariahReviewCase c " +
                        "WHERE c.status <> :archived " +
                        "AND c.caseStatus = :status " +
                        "AND c.submittedAt >= :fromDate",
                Long.class
        ).setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("status", ShariahCaseStatus.PENDING_REVIEW)
                .setParameter("fromDate", LocalDateTime.now().minusDays(7))
                .getSingleResult();
    }

    public List<ShariahModuleSummaryResponse> moduleBreakdown() {
        List<Object[]> rows = entityManager.createQuery(
                "SELECT UPPER(c.referenceModule), COUNT(c.id) " +
                        "FROM ShariahReviewCase c " +
                        "WHERE c.status <> :archived " +
                        "GROUP BY UPPER(c.referenceModule) " +
                        "ORDER BY COUNT(c.id) DESC",
                Object[].class
        ).setParameter("archived", RecordStatus.ARCHIVED).getResultList();

        return rows.stream()
                .map(row -> new ShariahModuleSummaryResponse(String.valueOf(row[0]), (Long) row[1]))
                .toList();
    }
}
