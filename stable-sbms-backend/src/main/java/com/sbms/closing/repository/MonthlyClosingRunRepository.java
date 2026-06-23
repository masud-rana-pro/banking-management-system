package com.sbms.closing.repository;

import com.sbms.closing.entity.MonthlyClosingRun;
import com.sbms.closing.enums.MonthlyClosingStatus;
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
public class MonthlyClosingRunRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public MonthlyClosingRun save(MonthlyClosingRun entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return entity;
        }
        return entityManager.merge(entity);
    }

    public Optional<MonthlyClosingRun> findById(Long id) {
        return Optional.ofNullable(entityManager.find(MonthlyClosingRun.class, id));
    }

    public Optional<MonthlyClosingRun> findByBranchIdAndClosingMonth(Long branchId, LocalDate closingMonth) {
        List<MonthlyClosingRun> result = entityManager.createQuery(
                        "from MonthlyClosingRun m where m.branchId = :branchId and m.closingMonth = :closingMonth",
                        MonthlyClosingRun.class
                )
                .setParameter("branchId", branchId)
                .setParameter("closingMonth", closingMonth)
                .setMaxResults(1)
                .getResultList();
        return result.stream().findFirst();
    }

    public List<MonthlyClosingRun> findAll(Long branchId, String status, LocalDate closingMonth) {
        StringBuilder hql = new StringBuilder("from MonthlyClosingRun m where 1=1");
        if (branchId != null) {
            hql.append(" and m.branchId = :branchId");
        }
        if (status != null && !status.isBlank()) {
            hql.append(" and upper(cast(m.status as string)) = :status");
        }
        if (closingMonth != null) {
            hql.append(" and m.closingMonth = :closingMonth");
        }
        hql.append(" order by m.closingMonth desc, m.branchName asc");
        TypedQuery<MonthlyClosingRun> query = entityManager.createQuery(hql.toString(), MonthlyClosingRun.class);
        if (branchId != null) {
            query.setParameter("branchId", branchId);
        }
        if (status != null && !status.isBlank()) {
            query.setParameter("status", status.trim().toUpperCase());
        }
        if (closingMonth != null) {
            query.setParameter("closingMonth", closingMonth);
        }
        return query.getResultList();
    }

    public long countByStatusAndMonth(MonthlyClosingStatus status, LocalDate monthStart) {
        return countByStatusAndMonth(status, monthStart, null);
    }

    public long countByStatusAndMonth(MonthlyClosingStatus status, LocalDate monthStart, Long branchId) {
        StringBuilder hql = new StringBuilder(
                "select count(m.id) from MonthlyClosingRun m where m.status = :status and m.closingMonth = :closingMonth"
        );
        if (branchId != null) {
            hql.append(" and m.branchId = :branchId");
        }
        TypedQuery<Long> query = entityManager.createQuery(
                        hql.toString(),
                        Long.class
                )
                .setParameter("status", status)
                .setParameter("closingMonth", monthStart);
        if (branchId != null) {
            query.setParameter("branchId", branchId);
        }
        Long count = query.getSingleResult();
        return count == null ? 0L : count;
    }
}
