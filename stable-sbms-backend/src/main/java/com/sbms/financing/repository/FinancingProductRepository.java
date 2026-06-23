package com.sbms.financing.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.financing.entity.FinancingProduct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class FinancingProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public FinancingProduct save(FinancingProduct entity) {
        entityManager.persist(entity);
        return entity;
    }

    public FinancingProduct update(FinancingProduct entity) {
        return entityManager.merge(entity);
    }

    public Optional<FinancingProduct> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT p FROM FinancingProduct p WHERE p.id = :id",
                            FinancingProduct.class
                    ).setParameter("id", id).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<FinancingProduct> findByProductCode(String productCode) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT p FROM FinancingProduct p WHERE LOWER(p.productCode) = :productCode",
                            FinancingProduct.class
                    ).setParameter("productCode", productCode.toLowerCase()).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<FinancingProduct> findAll() {
        return entityManager.createQuery(
                        "SELECT p FROM FinancingProduct p ORDER BY p.id DESC",
                        FinancingProduct.class
                ).getResultList();
    }

    public List<FinancingProduct> findLatest(int limit) {
        return entityManager.createQuery(
                        "SELECT p FROM FinancingProduct p ORDER BY p.id DESC",
                        FinancingProduct.class
                ).setMaxResults(limit).getResultList();
    }

    public String findLastProductCode() {
        List<String> result = entityManager.createQuery(
                        "SELECT p.productCode FROM FinancingProduct p " +
                                "WHERE p.productCode LIKE :prefix ORDER BY p.productCode DESC",
                        String.class
                ).setParameter("prefix", "FNP-%").setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public Long countActiveOrPending() {
        return entityManager.createQuery(
                        "SELECT COUNT(p.id) FROM FinancingProduct p WHERE p.status <> :archived",
                        Long.class
                ).setParameter("archived", RecordStatus.ARCHIVED).getSingleResult();
    }
}
