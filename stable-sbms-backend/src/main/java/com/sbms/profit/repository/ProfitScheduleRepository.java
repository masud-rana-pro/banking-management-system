package com.sbms.profit.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.profit.entity.ProfitSchedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ProfitScheduleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ProfitSchedule save(ProfitSchedule entity) {
        entityManager.persist(entity);
        return entity;
    }

    public ProfitSchedule update(ProfitSchedule entity) {
        return entityManager.merge(entity);
    }

    public Optional<ProfitSchedule> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT s FROM ProfitSchedule s " +
                                    "JOIN FETCH s.account a " +
                                    "JOIN FETCH a.customer c " +
                                    "JOIN FETCH a.accountType at " +
                                    "WHERE s.id = :id",
                            ProfitSchedule.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<ProfitSchedule> findAll() {
        return entityManager.createQuery(
                        "SELECT s FROM ProfitSchedule s " +
                                "JOIN FETCH s.account a " +
                                "JOIN FETCH a.customer c " +
                                "JOIN FETCH a.accountType at " +
                                "ORDER BY s.id DESC",
                        ProfitSchedule.class
                )
                .getResultList();
    }

    public Optional<ProfitSchedule> findByAccountId(Long accountId) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT s FROM ProfitSchedule s " +
                                    "JOIN FETCH s.account a " +
                                    "JOIN FETCH a.customer c " +
                                    "JOIN FETCH a.accountType at " +
                                    "WHERE a.id = :accountId AND s.status <> :archived",
                            ProfitSchedule.class
                    )
                    .setParameter("accountId", accountId)
                    .setParameter("archived", RecordStatus.ARCHIVED)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<ProfitSchedule> findDueSchedules(LocalDate postingDate) {
        return entityManager.createQuery(
                        "SELECT s FROM ProfitSchedule s " +
                                "JOIN FETCH s.account a " +
                                "JOIN FETCH a.customer c " +
                                "JOIN FETCH a.accountType at " +
                                "WHERE s.status = :active " +
                                "AND s.nextPostingDate <= :postingDate " +
                                "ORDER BY s.nextPostingDate ASC, s.id ASC",
                        ProfitSchedule.class
                )
                .setParameter("active", RecordStatus.ACTIVE)
                .setParameter("postingDate", postingDate)
                .getResultList();
    }

    public Long countDueSchedules(LocalDate postingDate) {
        return entityManager.createQuery(
                        "SELECT COUNT(s.id) FROM ProfitSchedule s " +
                                "WHERE s.status = :active AND s.nextPostingDate <= :postingDate",
                        Long.class
                )
                .setParameter("active", RecordStatus.ACTIVE)
                .setParameter("postingDate", postingDate)
                .getSingleResult();
    }

    public Optional<LocalDate> findUpcomingDate() {
        List<LocalDate> dates = entityManager.createQuery(
                        "SELECT s.nextPostingDate FROM ProfitSchedule s " +
                                "WHERE s.status = :active " +
                                "ORDER BY s.nextPostingDate ASC",
                        LocalDate.class
                )
                .setParameter("active", RecordStatus.ACTIVE)
                .setMaxResults(1)
                .getResultList();
        return dates.isEmpty() ? Optional.empty() : Optional.ofNullable(dates.get(0));
    }

    public Long countByAccountTypeId(Long accountTypeId) {
        return entityManager.createQuery(
                        "SELECT COUNT(s.id) FROM ProfitSchedule s " +
                                "WHERE s.account.accountType.id = :accountTypeId " +
                                "AND s.status <> :archived",
                        Long.class
                )
                .setParameter("accountTypeId", accountTypeId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }
}
