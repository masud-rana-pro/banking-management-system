package com.sbms.financing.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.financing.entity.FinancingApplication;
import com.sbms.financing.enums.FinancingApplicationStatus;
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
public class FinancingApplicationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public FinancingApplication save(FinancingApplication entity) {
        entityManager.persist(entity);
        return entity;
    }

    public FinancingApplication update(FinancingApplication entity) {
        return entityManager.merge(entity);
    }

    public Optional<FinancingApplication> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT a FROM FinancingApplication a " +
                                    "JOIN FETCH a.customer c " +
                                    "JOIN FETCH a.product p " +
                                    "WHERE a.id = :id",
                            FinancingApplication.class
                    ).setParameter("id", id).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<FinancingApplication> findAll(Long productId, Long customerId, Long branchId, String keyword) {
        StringBuilder hql = new StringBuilder(
                "SELECT a FROM FinancingApplication a " +
                        "JOIN FETCH a.customer c " +
                        "JOIN FETCH a.product p " +
                        "WHERE 1=1 "
        );

        if (productId != null) hql.append("AND p.id = :productId ");
        if (customerId != null) hql.append("AND c.id = :customerId ");
        if (branchId != null) hql.append("AND a.branchId = :branchId ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("AND (LOWER(a.applicationNo) LIKE :keyword OR LOWER(c.customerCode) LIKE :keyword OR LOWER(c.fullName) LIKE :keyword OR LOWER(p.productCode) LIKE :keyword OR LOWER(p.productName) LIKE :keyword) ");
        }
        hql.append("ORDER BY a.id DESC");

        TypedQuery<FinancingApplication> query = entityManager.createQuery(hql.toString(), FinancingApplication.class);
        if (productId != null) query.setParameter("productId", productId);
        if (customerId != null) query.setParameter("customerId", customerId);
        if (branchId != null) query.setParameter("branchId", branchId);
        if (keyword != null && !keyword.trim().isEmpty()) query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        return query.getResultList();
    }

    public List<FinancingApplication> findLatest(int limit) {
        return entityManager.createQuery(
                        "SELECT a FROM FinancingApplication a " +
                                "JOIN FETCH a.customer c " +
                                "JOIN FETCH a.product p " +
                                "ORDER BY a.id DESC",
                        FinancingApplication.class
                ).setMaxResults(limit).getResultList();
    }

    public String findLastApplicationNo() {
        List<String> result = entityManager.createQuery(
                        "SELECT a.applicationNo FROM FinancingApplication a " +
                                "WHERE a.applicationNo LIKE :prefix ORDER BY a.applicationNo DESC",
                        String.class
                ).setParameter("prefix", "FNA-%").setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public Long countPendingApplications() {
        return entityManager.createQuery(
                        "SELECT COUNT(a.id) FROM FinancingApplication a " +
                                "WHERE a.status <> :archived " +
                                "AND a.applicationStatus IN :statuses",
                        Long.class
                ).setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("statuses", List.of(
                        FinancingApplicationStatus.DRAFT,
                        FinancingApplicationStatus.SUBMITTED,
                        FinancingApplicationStatus.DOC_CHECK,
                        FinancingApplicationStatus.ASSET_VERIFIED,
                        FinancingApplicationStatus.SHARIAH_REVIEW,
                        FinancingApplicationStatus.RETURNED
                )).getSingleResult();
    }

    public Long countApprovedApplications() {
        return entityManager.createQuery(
                        "SELECT COUNT(a.id) FROM FinancingApplication a " +
                                "WHERE a.status <> :archived AND a.applicationStatus = :status",
                        Long.class
                ).setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("status", FinancingApplicationStatus.APPROVED)
                .getSingleResult();
    }

    public List<Object[]> countByProduct() {
        return entityManager.createQuery(
                        "SELECT p.productName, COUNT(a.id) FROM FinancingApplication a " +
                                "JOIN a.product p " +
                                "WHERE a.status <> :archived " +
                                "GROUP BY p.productName ORDER BY COUNT(a.id) DESC",
                        Object[].class
                ).setParameter("archived", RecordStatus.ARCHIVED).getResultList();
    }
}
