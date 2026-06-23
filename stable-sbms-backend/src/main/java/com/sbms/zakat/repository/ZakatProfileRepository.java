package com.sbms.zakat.repository;

import com.sbms.zakat.entity.ZakatProfile;
import com.sbms.zakat.enums.ZakatCalculationStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ZakatProfileRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ZakatProfile save(ZakatProfile entity) {
        entityManager.persist(entity);
        return entity;
    }

    public ZakatProfile update(ZakatProfile entity) {
        return entityManager.merge(entity);
    }

    public Optional<ZakatProfile> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT z FROM ZakatProfile z JOIN FETCH z.customer c WHERE z.id = :id",
                    ZakatProfile.class
            ).setParameter("id", id).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<ZakatProfile> findByCustomerAndYear(Long customerId, Integer zakatYear) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT z FROM ZakatProfile z JOIN FETCH z.customer c " +
                            "WHERE c.id = :customerId AND z.zakatYear = :zakatYear",
                    ZakatProfile.class
            ).setParameter("customerId", customerId)
                    .setParameter("zakatYear", zakatYear)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<ZakatProfile> findAll(Long customerId, Integer zakatYear, String keyword) {
        StringBuilder hql = new StringBuilder(
                "SELECT z FROM ZakatProfile z JOIN FETCH z.customer c WHERE 1=1 "
        );
        if (customerId != null) hql.append("AND c.id = :customerId ");
        if (zakatYear != null) hql.append("AND z.zakatYear = :zakatYear ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("AND (LOWER(c.customerCode) LIKE :keyword OR LOWER(c.fullName) LIKE :keyword OR str(z.zakatYear) LIKE :keyword) ");
        }
        hql.append("ORDER BY z.id DESC");

        TypedQuery<ZakatProfile> query = entityManager.createQuery(hql.toString(), ZakatProfile.class);
        if (customerId != null) query.setParameter("customerId", customerId);
        if (zakatYear != null) query.setParameter("zakatYear", zakatYear);
        if (keyword != null && !keyword.trim().isEmpty()) query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        return query.getResultList();
    }

    public List<ZakatProfile> findLatest(int limit) {
        return entityManager.createQuery(
                "SELECT z FROM ZakatProfile z JOIN FETCH z.customer c ORDER BY z.id DESC",
                ZakatProfile.class
        ).setMaxResults(limit).getResultList();
    }

    public Long countDueAccounts() {
        return entityManager.createQuery(
                "SELECT COUNT(z.id) FROM ZakatProfile z WHERE z.zakatAmount > 0 AND z.calculationStatus IN :statuses",
                Long.class
        ).setParameter("statuses", List.of(ZakatCalculationStatus.CALCULATED, ZakatCalculationStatus.DEDUCTED))
                .getSingleResult();
    }

    public BigDecimal sumCalculatedAmount() {
        BigDecimal value = entityManager.createQuery(
                "SELECT COALESCE(SUM(z.zakatAmount), 0) FROM ZakatProfile z WHERE z.calculationStatus IN :statuses",
                BigDecimal.class
        ).setParameter("statuses", List.of(ZakatCalculationStatus.CALCULATED, ZakatCalculationStatus.DEDUCTED))
                .getSingleResult();
        return value == null ? BigDecimal.ZERO : value;
    }

    public Long countUpcomingReminders() {
        int currentYear = Year.now().getValue();
        return entityManager.createQuery(
                "SELECT COUNT(z.id) FROM ZakatProfile z " +
                        "WHERE z.zakatYear <= :currentYear AND z.calculationStatus IN :statuses",
                Long.class
        ).setParameter("currentYear", currentYear)
                .setParameter("statuses", List.of(ZakatCalculationStatus.PROFILED, ZakatCalculationStatus.BELOW_NISAB))
                .getSingleResult();
    }
}
