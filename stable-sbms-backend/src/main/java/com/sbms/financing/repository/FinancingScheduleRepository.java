package com.sbms.financing.repository;

import com.sbms.financing.entity.FinancingSchedule;
import com.sbms.financing.enums.FinancingScheduleStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class FinancingScheduleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void saveAll(List<FinancingSchedule> entities) {
        for (int i = 0; i < entities.size(); i++) {
            entityManager.persist(entities.get(i));
            if ((i + 1) % 20 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    public FinancingSchedule update(FinancingSchedule entity) {
        return entityManager.merge(entity);
    }

    public Optional<FinancingSchedule> findById(Long id) {
        List<FinancingSchedule> items = entityManager.createQuery(
                        "SELECT s FROM FinancingSchedule s " +
                                "JOIN FETCH s.application a " +
                                "JOIN FETCH a.customer c " +
                                "JOIN FETCH a.product p " +
                                "WHERE s.id = :id",
                        FinancingSchedule.class
                )
                .setParameter("id", id)
                .getResultList();
        return items.stream().findFirst();
    }

    public List<FinancingSchedule> findByApplicationId(Long applicationId) {
        return entityManager.createQuery(
                        "SELECT s FROM FinancingSchedule s " +
                                "JOIN FETCH s.application a " +
                                "WHERE a.id = :applicationId ORDER BY s.installmentNo ASC",
                        FinancingSchedule.class
                ).setParameter("applicationId", applicationId).getResultList();
    }

    public List<FinancingSchedule> findPaidWithProfit() {
        return entityManager.createQuery(
                        "SELECT s FROM FinancingSchedule s " +
                                "JOIN FETCH s.application a " +
                                "JOIN FETCH a.customer c " +
                                "JOIN FETCH a.product p " +
                                "WHERE s.scheduleStatus = :status " +
                                "AND s.profitAmount > 0 " +
                                "ORDER BY s.id ASC",
                        FinancingSchedule.class
                )
                .setParameter("status", FinancingScheduleStatus.PAID)
                .getResultList();
    }

    public Long countOverdueInstallments(LocalDate currentDate) {
        return entityManager.createQuery(
                        "SELECT COUNT(s.id) FROM FinancingSchedule s " +
                                "WHERE s.dueDate < :currentDate AND s.scheduleStatus IN :statuses",
                        Long.class
                ).setParameter("currentDate", currentDate)
                .setParameter("statuses", List.of(FinancingScheduleStatus.PENDING, FinancingScheduleStatus.PARTIAL, FinancingScheduleStatus.OVERDUE))
                .getSingleResult();
    }

    public BigDecimal sumCharityAmount() {
        BigDecimal result = entityManager.createQuery(
                        "SELECT COALESCE(SUM(s.charityAmount), 0) FROM FinancingSchedule s",
                        BigDecimal.class
                ).getSingleResult();
        return result == null ? BigDecimal.ZERO : result;
    }
}
