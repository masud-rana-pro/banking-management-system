package com.sbms.branch.repository;

import com.sbms.branch.entity.VaultBalance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
@Transactional
public class VaultBalanceRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public VaultBalance save(VaultBalance entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return entity;
        }
        return entityManager.merge(entity);
    }

    public Optional<VaultBalance> findById(Long id) {
        List<VaultBalance> list = entityManager
                .createQuery("from VaultBalance v where v.id = :id", VaultBalance.class)
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList();

        return list.stream().findFirst();
    }

    public boolean existsByBranchAndDate(Long branchId, LocalDate balanceDate) {
        String hql = "select count(v.id) from VaultBalance v " +
                "where v.branchId = :branchId and v.balanceDate = :balanceDate";

        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("branchId", branchId)
                .setParameter("balanceDate", balanceDate)
                .getSingleResult();

        return count > 0;
    }

    public List<VaultBalance> findAll(Long branchId, String status, Boolean isClosed) {
        String hql = "from VaultBalance v where 1=1";

        if (branchId != null && branchId > 0) {
            hql += " and v.branchId = :branchId";
        }

        if (status != null && !status.isBlank()) {
            hql += " and v.status = :status";
        }

        if (isClosed != null) {
            hql += " and v.isClosed = :isClosed";
        }

        hql += " order by v.balanceDate desc, v.id desc";

        TypedQuery<VaultBalance> query = entityManager.createQuery(hql, VaultBalance.class);

        if (branchId != null && branchId > 0) {
            query.setParameter("branchId", branchId);
        }

        if (status != null && !status.isBlank()) {
            query.setParameter("status", status);
        }

        if (isClosed != null) {
            query.setParameter("isClosed", isClosed);
        }

        return query.getResultList();
    }
    
    public BigDecimal getCurrentBranchCashPosition() {
        String hql = "select coalesce(sum(v.closingBalance), 0) from VaultBalance v " +
                "where v.status in ('ACTIVE', 'CLOSED')";

        return entityManager.createQuery(hql, BigDecimal.class)
                .getSingleResult();
    }

    public Long countTodayVaultOpened(LocalDate today) {
        String hql = "select count(v.id) from VaultBalance v " +
                "where v.balanceDate = :today";

        return entityManager.createQuery(hql, Long.class)
                .setParameter("today", today)
                .getSingleResult();
    }

    public Long countTodayVaultClosed(LocalDate today) {
        String hql = "select count(v.id) from VaultBalance v " +
                "where v.balanceDate = :today " +
                "and v.isClosed = true";

        return entityManager.createQuery(hql, Long.class)
                .setParameter("today", today)
                .getSingleResult();
    }

    public Long countTodayVaultPendingClose(LocalDate today) {
        String hql = "select count(v.id) from VaultBalance v " +
                "where v.balanceDate = :today " +
                "and v.isClosed = false";

        return entityManager.createQuery(hql, Long.class)
                .setParameter("today", today)
                .getSingleResult();
    }
}