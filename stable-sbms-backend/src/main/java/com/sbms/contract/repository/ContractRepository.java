package com.sbms.contract.repository;

import com.sbms.contract.entity.Contract;
import com.sbms.contract.enums.ContractStatus;
import com.sbms.customer.enums.RecordStatus;
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
public class ContractRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Contract save(Contract entity) {
        entityManager.persist(entity);
        return entity;
    }

    public Contract update(Contract entity) {
        return entityManager.merge(entity);
    }

    public Optional<Contract> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT c FROM Contract c " +
                            "JOIN FETCH c.template t " +
                            "JOIN FETCH c.customer cu " +
                            "WHERE c.id = :id",
                    Contract.class
            ).setParameter("id", id).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<Contract> findAll(Long templateId, Long customerId, String referenceModule, String keyword) {
        StringBuilder hql = new StringBuilder(
                "SELECT c FROM Contract c " +
                        "JOIN FETCH c.template t " +
                        "JOIN FETCH c.customer cu " +
                        "WHERE 1=1 "
        );
        if (templateId != null) hql.append("AND t.id = :templateId ");
        if (customerId != null) hql.append("AND cu.id = :customerId ");
        if (referenceModule != null && !referenceModule.trim().isEmpty()) hql.append("AND UPPER(c.referenceModule) = :referenceModule ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("AND (LOWER(c.contractNo) LIKE :keyword OR LOWER(cu.customerCode) LIKE :keyword OR LOWER(cu.fullName) LIKE :keyword OR LOWER(t.templateCode) LIKE :keyword OR LOWER(t.templateName) LIKE :keyword) ");
        }
        hql.append("ORDER BY c.id DESC");

        TypedQuery<Contract> query = entityManager.createQuery(hql.toString(), Contract.class);
        if (templateId != null) query.setParameter("templateId", templateId);
        if (customerId != null) query.setParameter("customerId", customerId);
        if (referenceModule != null && !referenceModule.trim().isEmpty()) query.setParameter("referenceModule", referenceModule.trim().toUpperCase());
        if (keyword != null && !keyword.trim().isEmpty()) query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        return query.getResultList();
    }

    public List<Contract> findLatest(int limit) {
        return entityManager.createQuery(
                "SELECT c FROM Contract c " +
                        "JOIN FETCH c.template t " +
                        "JOIN FETCH c.customer cu " +
                        "ORDER BY c.id DESC",
                Contract.class
        ).setMaxResults(limit).getResultList();
    }

    public String findLastContractNo() {
        List<String> result = entityManager.createQuery(
                "SELECT c.contractNo FROM Contract c WHERE c.contractNo LIKE :prefix ORDER BY c.contractNo DESC",
                String.class
        ).setParameter("prefix", "CTR-%").setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public Long countTotal() {
        return entityManager.createQuery(
                "SELECT COUNT(c.id) FROM Contract c WHERE c.status <> :archived",
                Long.class
        ).setParameter("archived", RecordStatus.ARCHIVED).getSingleResult();
    }

    public Long countDraft() {
        return entityManager.createQuery(
                "SELECT COUNT(c.id) FROM Contract c WHERE c.status <> :archived AND c.contractStatus = :status",
                Long.class
        ).setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("status", ContractStatus.DRAFT)
                .getSingleResult();
    }

    public Long countActiveLocked() {
        return entityManager.createQuery(
                "SELECT COUNT(c.id) FROM Contract c WHERE c.status <> :archived AND c.contractStatus IN :statuses",
                Long.class
        ).setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("statuses", List.of(ContractStatus.ACTIVE, ContractStatus.LOCKED))
                .getSingleResult();
    }

    public Long countPendingSignatures() {
        return entityManager.createQuery(
                "SELECT COUNT(c.id) FROM Contract c " +
                        "WHERE c.status <> :archived " +
                        "AND (c.signedByCustomer IS NULL OR c.signedByShariah IS NULL)",
                Long.class
        ).setParameter("archived", RecordStatus.ARCHIVED).getSingleResult();
    }
}
