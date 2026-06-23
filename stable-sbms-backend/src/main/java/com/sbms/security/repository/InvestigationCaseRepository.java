package com.sbms.security.repository;

import com.sbms.security.entity.InvestigationCase;
import com.sbms.security.enums.InvestigationCaseStatus;
import com.sbms.security.enums.InvestigationCaseType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class InvestigationCaseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public InvestigationCase save(InvestigationCase entity) {
        entityManager.persist(entity);
        return entity;
    }

    public InvestigationCase update(InvestigationCase entity) {
        return entityManager.merge(entity);
    }

    public Optional<InvestigationCase> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT c FROM InvestigationCase c WHERE c.id = :id",
                            InvestigationCase.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<InvestigationCase> findAll(String caseStatus, String caseType, String keyword) {
        StringBuilder hql = new StringBuilder("SELECT c FROM InvestigationCase c WHERE 1 = 1");
        if (caseStatus != null && !caseStatus.trim().isEmpty()) {
            hql.append(" AND c.caseStatus = :caseStatus");
        }
        if (caseType != null && !caseType.trim().isEmpty()) {
            hql.append(" AND c.caseType = :caseType");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("""
                     AND (
                        LOWER(c.caseNo) LIKE :keyword
                        OR LOWER(c.referenceModule) LIKE :keyword
                        OR LOWER(c.openedBy) LIKE :keyword
                        OR LOWER(COALESCE(c.remarks, '')) LIKE :keyword
                     )
                    """);
        }
        hql.append(" ORDER BY c.openedAt DESC, c.id DESC");

        var query = entityManager.createQuery(hql.toString(), InvestigationCase.class);
        if (caseStatus != null && !caseStatus.trim().isEmpty()) {
            query.setParameter("caseStatus", InvestigationCaseStatus.valueOf(caseStatus.trim().toUpperCase()));
        }
        if (caseType != null && !caseType.trim().isEmpty()) {
            query.setParameter("caseType", InvestigationCaseType.valueOf(caseType.trim().toUpperCase()));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public Long countOpenCases() {
        return entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM InvestigationCase c WHERE c.caseStatus <> :closedStatus",
                        Long.class
                )
                .setParameter("closedStatus", InvestigationCaseStatus.CLOSED)
                .getSingleResult();
    }

    public String findLastCaseNo() {
        List<String> caseNos = entityManager.createQuery(
                        "SELECT c.caseNo FROM InvestigationCase c ORDER BY c.id DESC",
                        String.class
                )
                .setMaxResults(1)
                .getResultList();
        return caseNos.isEmpty() ? null : caseNos.get(0);
    }

    public List<InvestigationCase> findRecent(int limit) {
        return entityManager.createQuery(
                        "SELECT c FROM InvestigationCase c ORDER BY c.openedAt DESC, c.id DESC",
                        InvestigationCase.class
                )
                .setMaxResults(limit)
                .getResultList();
    }
}
