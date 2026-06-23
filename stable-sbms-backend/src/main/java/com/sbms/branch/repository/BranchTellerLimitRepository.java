package com.sbms.branch.repository;

import com.sbms.branch.entity.BranchTellerLimit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class BranchTellerLimitRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public BranchTellerLimit save(BranchTellerLimit entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return entity;
        }
        return entityManager.merge(entity);
    }

    public Optional<BranchTellerLimit> findById(Long id) {
        List<BranchTellerLimit> list = entityManager
                .createQuery("from BranchTellerLimit t where t.id = :id", BranchTellerLimit.class)
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList();

        return list.stream().findFirst();
    }

    public Long countTellerLimitAlerts() {
        String hql = "select count(t.id) from BranchTellerLimit t " +
                "where t.status = 'ACTIVE' " +
                "and (t.dailyDepositLimit <= 0 " +
                "or t.dailyWithdrawLimit <= 0 " +
                "or t.singleTxnLimit <= 0 " +
                "or t.singleTxnLimit > t.dailyDepositLimit " +
                "or t.singleTxnLimit > t.dailyWithdrawLimit)";

        return entityManager.createQuery(hql, Long.class)
                .getSingleResult();
    }
    
    public List<BranchTellerLimit> findAll(Long branchId, Long userId, String status) {
        String hql = "from BranchTellerLimit t where 1=1";

        if (branchId != null) hql += " and t.branchId = :branchId";
        if (userId != null) hql += " and t.userId = :userId";
        if (status != null && !status.isBlank()) hql += " and t.status = :status";

        hql += " order by t.limitDate desc, t.id desc";

        TypedQuery<BranchTellerLimit> query = entityManager.createQuery(hql, BranchTellerLimit.class);

        if (branchId != null) query.setParameter("branchId", branchId);
        if (userId != null) query.setParameter("userId", userId);
        if (status != null && !status.isBlank()) query.setParameter("status", status);

        return query.getResultList();
    }

    public boolean existsByBranchUserDate(Long branchId, Long userId, LocalDate limitDate, Long excludeId) {
        String hql = "select count(t.id) from BranchTellerLimit t " +
                "where t.branchId = :branchId " +
                "and t.userId = :userId " +
                "and t.limitDate = :limitDate " +
                "and (:excludeId is null or t.id <> :excludeId)";

        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("branchId", branchId)
                .setParameter("userId", userId)
                .setParameter("limitDate", limitDate)
                .setParameter("excludeId", excludeId)
                .getSingleResult();

        return count > 0;
    }

    public void deactivate(Long id) {
        entityManager.createQuery(
                "update BranchTellerLimit t set t.status = 'INACTIVE' where t.id = :id"
        ).setParameter("id", id).executeUpdate();
    }
}