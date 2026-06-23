package com.sbms.branch.repository;

import com.sbms.branch.entity.BranchCashLedger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class BranchCashLedgerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public BranchCashLedger save(BranchCashLedger entity) {
        entityManager.persist(entity);
        return entity;
    }

    public List<BranchCashLedger> findAll(Long branchId, String entryType, String sourceType) {
        String hql = "from BranchCashLedger l where 1=1";

        if (branchId != null && branchId > 0) {
            hql += " and l.branchId = :branchId";
        }

        if (entryType != null && !entryType.isBlank()) {
            hql += " and l.entryType = :entryType";
        }

        if (sourceType != null && !sourceType.isBlank()) {
            hql += " and l.sourceType = :sourceType";
        }

        hql += " order by l.ledgerDate desc, l.id desc";

        TypedQuery<BranchCashLedger> query = entityManager.createQuery(hql, BranchCashLedger.class);

        if (branchId != null && branchId > 0) {
            query.setParameter("branchId", branchId);
        }

        if (entryType != null && !entryType.isBlank()) {
            query.setParameter("entryType", entryType);
        }

        if (sourceType != null && !sourceType.isBlank()) {
            query.setParameter("sourceType", sourceType);
        }

        return query.getResultList();
    }
}