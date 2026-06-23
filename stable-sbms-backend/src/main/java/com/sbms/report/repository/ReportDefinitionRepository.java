package com.sbms.report.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.report.entity.ReportDefinition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ReportDefinitionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ReportDefinition save(ReportDefinition entity) {
        entityManager.persist(entity);
        return entity;
    }

    public ReportDefinition update(ReportDefinition entity) {
        return entityManager.merge(entity);
    }

    public Optional<ReportDefinition> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT r FROM ReportDefinition r WHERE r.id = :id",
                            ReportDefinition.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<ReportDefinition> findByQueryKey(String queryKey) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT r FROM ReportDefinition r WHERE UPPER(r.queryKey) = :queryKey",
                            ReportDefinition.class
                    )
                    .setParameter("queryKey", queryKey == null ? "" : queryKey.trim().toUpperCase())
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<ReportDefinition> findAll() {
        return entityManager.createQuery(
                        "SELECT r FROM ReportDefinition r ORDER BY r.reportType, r.reportName",
                        ReportDefinition.class
                )
                .getResultList();
    }

    public Long countActive() {
        return entityManager.createQuery(
                        "SELECT COUNT(r.id) FROM ReportDefinition r WHERE r.status = :status",
                        Long.class
                )
                .setParameter("status", RecordStatus.ACTIVE)
                .getSingleResult();
    }
}
