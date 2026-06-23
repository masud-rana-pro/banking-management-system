package com.sbms.depositscheme.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.depositscheme.entity.DepositSchemeSchedule;
import com.sbms.depositscheme.enums.DepositSchedulePaymentStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class DepositSchemeScheduleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void saveAll(List<DepositSchemeSchedule> entities) {
        for (int i = 0; i < entities.size(); i++) {
            entityManager.persist(entities.get(i));
            if ((i + 1) % 20 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    public List<DepositSchemeSchedule> findByEnrollmentId(Long enrollmentId) {
        return entityManager.createQuery(
                        "SELECT s FROM DepositSchemeSchedule s " +
                                "JOIN FETCH s.enrollment e " +
                                "WHERE e.id = :enrollmentId ORDER BY s.installmentNo ASC",
                        DepositSchemeSchedule.class
                )
                .setParameter("enrollmentId", enrollmentId)
                .getResultList();
    }

    public Long countDueInstallments(LocalDate currentDate) {
        return entityManager.createQuery(
                        "SELECT COUNT(s.id) FROM DepositSchemeSchedule s " +
                                "WHERE s.status = :active " +
                                "AND s.dueDate <= :currentDate " +
                                "AND s.paymentStatus IN :statuses",
                        Long.class
                )
                .setParameter("active", RecordStatus.ACTIVE)
                .setParameter("currentDate", currentDate)
                .setParameter("statuses", List.of(DepositSchedulePaymentStatus.PENDING, DepositSchedulePaymentStatus.OVERDUE))
                .getSingleResult();
    }

    public Long countByEnrollmentId(Long enrollmentId) {
        return entityManager.createQuery(
                        "SELECT COUNT(s.id) FROM DepositSchemeSchedule s WHERE s.enrollment.id = :enrollmentId",
                        Long.class
                )
                .setParameter("enrollmentId", enrollmentId)
                .getSingleResult();
    }

    public Long countPaidByEnrollmentId(Long enrollmentId) {
        return entityManager.createQuery(
                        "SELECT COUNT(s.id) FROM DepositSchemeSchedule s " +
                                "WHERE s.enrollment.id = :enrollmentId AND s.paymentStatus = :paid",
                        Long.class
                )
                .setParameter("enrollmentId", enrollmentId)
                .setParameter("paid", DepositSchedulePaymentStatus.PAID)
                .getSingleResult();
    }
}
