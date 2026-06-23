package com.sbms.contract.repository;

import com.sbms.contract.entity.ContractTemplate;
import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ContractTemplateRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ContractTemplate save(ContractTemplate entity) {
        entityManager.persist(entity);
        return entity;
    }

    public ContractTemplate update(ContractTemplate entity) {
        return entityManager.merge(entity);
    }

    public Optional<ContractTemplate> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT t FROM ContractTemplate t WHERE t.id = :id",
                    ContractTemplate.class
            ).setParameter("id", id).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<ContractTemplate> findByTemplateCode(String templateCode) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT t FROM ContractTemplate t WHERE LOWER(t.templateCode) = :templateCode",
                    ContractTemplate.class
            ).setParameter("templateCode", templateCode.toLowerCase()).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<ContractTemplate> findAll() {
        return entityManager.createQuery(
                "SELECT t FROM ContractTemplate t ORDER BY t.id DESC",
                ContractTemplate.class
        ).getResultList();
    }

    public List<ContractTemplate> findLatest(int limit) {
        return entityManager.createQuery(
                "SELECT t FROM ContractTemplate t ORDER BY t.id DESC",
                ContractTemplate.class
        ).setMaxResults(limit).getResultList();
    }

    public String findLastTemplateCode() {
        List<String> result = entityManager.createQuery(
                "SELECT t.templateCode FROM ContractTemplate t WHERE t.templateCode LIKE :prefix ORDER BY t.templateCode DESC",
                String.class
        ).setParameter("prefix", "CTM-%").setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public Long countActive() {
        return entityManager.createQuery(
                "SELECT COUNT(t.id) FROM ContractTemplate t WHERE t.status <> :archived",
                Long.class
        ).setParameter("archived", RecordStatus.ARCHIVED).getSingleResult();
    }
}
