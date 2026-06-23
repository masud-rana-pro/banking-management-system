package com.sbms.accounting.repository;

import com.sbms.accounting.entity.GlAccount;
import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class GlAccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public GlAccount save(GlAccount entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return entity;
        }
        return entityManager.merge(entity);
    }

    public Optional<GlAccount> findById(Long id) {
        return Optional.ofNullable(entityManager.find(GlAccount.class, id));
    }

    public Optional<GlAccount> findByCode(String accountCode) {
        List<GlAccount> items = entityManager.createQuery("""
                from GlAccount a
                where upper(a.accountCode) = :accountCode
                """, GlAccount.class)
            .setParameter("accountCode", accountCode == null ? "" : accountCode.trim().toUpperCase())
            .getResultList();
        return items.stream().findFirst();
    }

    public boolean existsByCode(String accountCode, Long ignoreId) {
        String hql = "select count(a.id) from GlAccount a where upper(a.accountCode) = :accountCode";
        if (ignoreId != null) {
            hql += " and a.id <> :ignoreId";
        }
        TypedQuery<Long> query = entityManager.createQuery(hql, Long.class)
                .setParameter("accountCode", accountCode == null ? "" : accountCode.trim().toUpperCase());
        if (ignoreId != null) {
            query.setParameter("ignoreId", ignoreId);
        }
        Long value = query.getSingleResult();
        return value != null && value > 0;
    }

    public List<GlAccount> findAll(String accountType, Boolean allowPosting, RecordStatus status, String keyword) {
        StringBuilder hql = new StringBuilder("from GlAccount a where 1=1");
        if (accountType != null && !accountType.isBlank()) {
            hql.append(" and upper(a.accountType) = :accountType");
        }
        if (allowPosting != null) {
            hql.append(" and a.allowPosting = :allowPosting");
        }
        if (status != null) {
            hql.append(" and a.status = :status");
        }
        if (keyword != null && !keyword.isBlank()) {
            hql.append(" and (upper(a.accountCode) like :keyword or upper(a.accountName) like :keyword or upper(coalesce(a.parentAccountCode, '')) like :keyword)");
        }
        hql.append(" order by a.accountCode asc");

        TypedQuery<GlAccount> query = entityManager.createQuery(hql.toString(), GlAccount.class);
        if (accountType != null && !accountType.isBlank()) {
            query.setParameter("accountType", accountType.trim().toUpperCase());
        }
        if (allowPosting != null) {
            query.setParameter("allowPosting", allowPosting);
        }
        if (status != null) {
            query.setParameter("status", status);
        }
        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", "%" + keyword.trim().toUpperCase() + "%");
        }
        return query.getResultList();
    }

    public long countAll() {
        Long value = entityManager.createQuery("select count(a.id) from GlAccount a", Long.class).getSingleResult();
        return value == null ? 0L : value;
    }

    public long countByType(String accountType) {
        Long value = entityManager.createQuery("""
                select count(a.id)
                from GlAccount a
                where upper(a.accountType) = :accountType
                """, Long.class)
            .setParameter("accountType", accountType.trim().toUpperCase())
            .getSingleResult();
        return value == null ? 0L : value;
    }
}
