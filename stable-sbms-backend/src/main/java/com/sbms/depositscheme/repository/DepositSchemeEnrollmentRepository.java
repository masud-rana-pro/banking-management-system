package com.sbms.depositscheme.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.depositscheme.entity.DepositSchemeEnrollment;
import com.sbms.depositscheme.enums.DepositEnrollmentStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class DepositSchemeEnrollmentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public DepositSchemeEnrollment save(DepositSchemeEnrollment entity) {
        entityManager.persist(entity);
        return entity;
    }

    public DepositSchemeEnrollment update(DepositSchemeEnrollment entity) {
        return entityManager.merge(entity);
    }

    public Optional<DepositSchemeEnrollment> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT e FROM DepositSchemeEnrollment e " +
                                    "JOIN FETCH e.scheme s " +
                                    "JOIN FETCH e.customer c " +
                                    "JOIN FETCH e.linkedAccount a " +
                                    "WHERE e.id = :id",
                            DepositSchemeEnrollment.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<DepositSchemeEnrollment> findAll(Long schemeId, Long customerId, Long accountId) {
        String hql = "SELECT e FROM DepositSchemeEnrollment e " +
                "JOIN FETCH e.scheme s " +
                "JOIN FETCH e.customer c " +
                "JOIN FETCH e.linkedAccount a " +
                "WHERE 1=1 ";

        if (schemeId != null) {
            hql += "AND s.id = :schemeId ";
        }
        if (customerId != null) {
            hql += "AND c.id = :customerId ";
        }
        if (accountId != null) {
            hql += "AND a.id = :accountId ";
        }
        hql += "ORDER BY e.id DESC";

        TypedQuery<DepositSchemeEnrollment> query = entityManager.createQuery(hql, DepositSchemeEnrollment.class);
        if (schemeId != null) {
            query.setParameter("schemeId", schemeId);
        }
        if (customerId != null) {
            query.setParameter("customerId", customerId);
        }
        if (accountId != null) {
            query.setParameter("accountId", accountId);
        }
        return query.getResultList();
    }

    public List<DepositSchemeEnrollment> findLatest(int limit) {
        return entityManager.createQuery(
                        "SELECT e FROM DepositSchemeEnrollment e " +
                                "JOIN FETCH e.scheme s " +
                                "JOIN FETCH e.customer c " +
                                "JOIN FETCH e.linkedAccount a " +
                                "ORDER BY e.id DESC",
                        DepositSchemeEnrollment.class
                )
                .setMaxResults(limit)
                .getResultList();
    }

    public String findLastEnrollmentNo() {
        List<String> result = entityManager.createQuery(
                        "SELECT e.enrollmentNo FROM DepositSchemeEnrollment e " +
                                "WHERE e.enrollmentNo LIKE :prefix ORDER BY e.enrollmentNo DESC",
                        String.class
                )
                .setParameter("prefix", "DSE-%")
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public Long countActiveEnrollments() {
        return entityManager.createQuery(
                        "SELECT COUNT(e.id) FROM DepositSchemeEnrollment e " +
                                "WHERE e.status = :active AND e.enrollmentStatus = :enrollmentStatus",
                        Long.class
                )
                .setParameter("active", RecordStatus.ACTIVE)
                .setParameter("enrollmentStatus", DepositEnrollmentStatus.ACTIVE)
                .getSingleResult();
    }

    public Long countByEnrollmentStatus(DepositEnrollmentStatus enrollmentStatus) {
        return entityManager.createQuery(
                        "SELECT COUNT(e.id) FROM DepositSchemeEnrollment e " +
                                "WHERE e.status <> :archived AND e.enrollmentStatus = :enrollmentStatus",
                        Long.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("enrollmentStatus", enrollmentStatus)
                .getSingleResult();
    }

    public Long countActiveBySchemeId(Long schemeId) {
        return entityManager.createQuery(
                        "SELECT COUNT(e.id) FROM DepositSchemeEnrollment e " +
                                "WHERE e.scheme.id = :schemeId AND e.status = :active AND e.enrollmentStatus = :enrollmentStatus",
                        Long.class
                )
                .setParameter("schemeId", schemeId)
                .setParameter("active", RecordStatus.ACTIVE)
                .setParameter("enrollmentStatus", DepositEnrollmentStatus.ACTIVE)
                .getSingleResult();
    }

    public Long countMaturedBySchemeId(Long schemeId) {
        return entityManager.createQuery(
                        "SELECT COUNT(e.id) FROM DepositSchemeEnrollment e " +
                                "WHERE e.scheme.id = :schemeId AND e.enrollmentStatus = :enrollmentStatus",
                        Long.class
                )
                .setParameter("schemeId", schemeId)
                .setParameter("enrollmentStatus", DepositEnrollmentStatus.MATURED)
                .getSingleResult();
    }
}
