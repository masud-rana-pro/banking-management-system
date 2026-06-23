package com.sbms.integration.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.integration.entity.IntegrationProvider;
import com.sbms.integration.enums.IntegrationProviderType;
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
public class IntegrationProviderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public IntegrationProvider save(IntegrationProvider entity) {
        entityManager.persist(entity);
        return entity;
    }

    public IntegrationProvider update(IntegrationProvider entity) {
        return entityManager.merge(entity);
    }

    public Optional<IntegrationProvider> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT p FROM IntegrationProvider p WHERE p.id = :id",
                    IntegrationProvider.class
            ).setParameter("id", id).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<IntegrationProvider> findByProviderCode(String providerCode) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT p FROM IntegrationProvider p WHERE LOWER(p.providerCode) = :providerCode",
                    IntegrationProvider.class
            ).setParameter("providerCode", providerCode.toLowerCase()).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<IntegrationProvider> findAll(String providerType, String status, String keyword) {
        StringBuilder hql = new StringBuilder("SELECT p FROM IntegrationProvider p WHERE 1=1 ");
        if (providerType != null && !providerType.trim().isEmpty()) hql.append("AND p.providerType = :providerType ");
        if (status != null && !status.trim().isEmpty()) hql.append("AND p.status = :status ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("AND (LOWER(p.providerCode) LIKE :keyword OR LOWER(p.providerName) LIKE :keyword OR LOWER(p.baseUrl) LIKE :keyword OR LOWER(COALESCE(p.username, '')) LIKE :keyword) ");
        }
        hql.append("ORDER BY p.id DESC");

        TypedQuery<IntegrationProvider> query = entityManager.createQuery(hql.toString(), IntegrationProvider.class);
        if (providerType != null && !providerType.trim().isEmpty()) {
            query.setParameter("providerType", IntegrationProviderType.valueOf(providerType.trim().toUpperCase()));
        }
        if (status != null && !status.trim().isEmpty()) {
            query.setParameter("status", RecordStatus.valueOf(status.trim().toUpperCase()));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public String findLastProviderCode() {
        List<String> result = entityManager.createQuery(
                "SELECT p.providerCode FROM IntegrationProvider p WHERE p.providerCode LIKE :prefix ORDER BY p.providerCode DESC",
                String.class
        ).setParameter("prefix", "INT-%").setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public long countByStatus(RecordStatus status) {
        return entityManager.createQuery(
                "SELECT COUNT(p.id) FROM IntegrationProvider p WHERE p.status = :status",
                Long.class
        ).setParameter("status", status).getSingleResult();
    }

    public long countByType(IntegrationProviderType providerType) {
        return entityManager.createQuery(
                "SELECT COUNT(p.id) FROM IntegrationProvider p WHERE p.providerType = :providerType",
                Long.class
        ).setParameter("providerType", providerType).getSingleResult();
    }

    public long countActiveByType(IntegrationProviderType providerType) {
        return entityManager.createQuery(
                "SELECT COUNT(p.id) FROM IntegrationProvider p WHERE p.providerType = :providerType AND p.status = :status",
                Long.class
        ).setParameter("providerType", providerType)
                .setParameter("status", RecordStatus.ACTIVE)
                .getSingleResult();
    }
}
