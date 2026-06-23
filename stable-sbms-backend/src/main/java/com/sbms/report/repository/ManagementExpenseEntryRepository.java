package com.sbms.report.repository;

import com.sbms.report.entity.ManagementExpenseEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class ManagementExpenseEntryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ManagementExpenseEntry save(ManagementExpenseEntry entity) {
        entityManager.persist(entity);
        return entity;
    }

    public java.util.Optional<ManagementExpenseEntry> findById(Long id) {
        return java.util.Optional.ofNullable(entityManager.find(ManagementExpenseEntry.class, id));
    }

    public List<ManagementExpenseEntry> findAll(LocalDate dateFrom, LocalDate dateTo, Long branchId, String expenseCategory, String keyword) {
        StringBuilder hql = new StringBuilder("from ManagementExpenseEntry e where 1=1");

        if (dateFrom != null) {
            hql.append(" and e.expenseDate >= :dateFrom");
        }
        if (dateTo != null) {
            hql.append(" and e.expenseDate <= :dateTo");
        }
        if (branchId != null && branchId > 0) {
            hql.append(" and e.branchId = :branchId");
        }
        if (expenseCategory != null && !expenseCategory.isBlank()) {
            hql.append(" and upper(e.expenseCategory) = :expenseCategory");
        }
        if (keyword != null && !keyword.isBlank()) {
            hql.append(" and (upper(coalesce(e.expenseCode, '')) like :keyword");
            hql.append(" or upper(coalesce(e.referenceNo, '')) like :keyword");
            hql.append(" or upper(coalesce(e.remarks, '')) like :keyword)");
        }

        hql.append(" order by e.expenseDate desc, e.id desc");

        TypedQuery<ManagementExpenseEntry> query = entityManager.createQuery(hql.toString(), ManagementExpenseEntry.class);
        if (dateFrom != null) {
            query.setParameter("dateFrom", dateFrom);
        }
        if (dateTo != null) {
            query.setParameter("dateTo", dateTo);
        }
        if (branchId != null && branchId > 0) {
            query.setParameter("branchId", branchId);
        }
        if (expenseCategory != null && !expenseCategory.isBlank()) {
            query.setParameter("expenseCategory", expenseCategory.trim().toUpperCase());
        }
        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", "%" + keyword.trim().toUpperCase() + "%");
        }
        return query.getResultList();
    }

    public BigDecimal sumAmount(LocalDate dateFrom, LocalDate dateTo) {
        return toBigDecimal(entityManager.createQuery("""
                select coalesce(sum(e.amount), 0)
                from ManagementExpenseEntry e
                where e.expenseDate between :dateFrom and :dateTo
                """, BigDecimal.class)
            .setParameter("dateFrom", dateFrom)
            .setParameter("dateTo", dateTo)
            .getSingleResult());
    }

    public long countEntries(LocalDate dateFrom, LocalDate dateTo) {
        Long value = entityManager.createQuery("""
                select coalesce(count(e.id), 0)
                from ManagementExpenseEntry e
                where e.expenseDate between :dateFrom and :dateTo
                """, Long.class)
            .setParameter("dateFrom", dateFrom)
            .setParameter("dateTo", dateTo)
            .getSingleResult();
        return value == null ? 0L : value;
    }

    private BigDecimal toBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
