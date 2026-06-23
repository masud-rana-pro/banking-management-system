package com.sbms.contract.repository;

import com.sbms.contract.entity.ContractVersion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ContractVersionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ContractVersion save(ContractVersion entity) {
        entityManager.persist(entity);
        return entity;
    }

    public List<ContractVersion> findByContractId(Long contractId) {
        return entityManager.createQuery(
                "SELECT v FROM ContractVersion v " +
                        "JOIN FETCH v.contract c " +
                        "WHERE c.id = :contractId ORDER BY v.versionNo DESC, v.id DESC",
                ContractVersion.class
        ).setParameter("contractId", contractId).getResultList();
    }

    public Long countAll() {
        return entityManager.createQuery(
                "SELECT COUNT(v.id) FROM ContractVersion v",
                Long.class
        ).getSingleResult();
    }

    public Integer findNextVersionNo(Long contractId) {
        Integer current = entityManager.createQuery(
                "SELECT MAX(v.versionNo) FROM ContractVersion v JOIN v.contract c WHERE c.id = :contractId",
                Integer.class
        ).setParameter("contractId", contractId).getSingleResult();
        return current == null ? 1 : current + 1;
    }
}
