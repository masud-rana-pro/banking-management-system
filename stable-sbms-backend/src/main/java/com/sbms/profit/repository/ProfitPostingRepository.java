package com.sbms.profit.repository;

import com.sbms.profit.entity.ProfitPosting;
import com.sbms.profit.enums.ProfitPostingStatus;
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
public class ProfitPostingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ProfitPosting save(ProfitPosting entity) {
        entityManager.persist(entity);
        return entity;
    }

    public ProfitPosting update(ProfitPosting entity) {
        return entityManager.merge(entity);
    }

    public Optional<ProfitPosting> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT p FROM ProfitPosting p " +
                                    "JOIN FETCH p.account a " +
                                    "JOIN FETCH a.customer c " +
                                    "JOIN FETCH a.accountType at " +
                                    "JOIN FETCH p.schedule s " +
                                    "WHERE p.id = :id",
                            ProfitPosting.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<ProfitPosting> findAll() {
        return entityManager.createQuery(
                        "SELECT p FROM ProfitPosting p " +
                                "JOIN FETCH p.account a " +
                                "JOIN FETCH a.customer c " +
                                "JOIN FETCH a.accountType at " +
                                "JOIN FETCH p.schedule s " +
                                "ORDER BY p.id DESC",
                        ProfitPosting.class
                )
                .getResultList();
    }

    public Optional<ProfitPosting> findByPostingRef(String postingRef) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT p FROM ProfitPosting p WHERE p.postingRef = :postingRef",
                            ProfitPosting.class
                    )
                    .setParameter("postingRef", postingRef)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<ProfitPosting> findByScheduleAndPeriod(Long scheduleId, LocalDate periodFrom, LocalDate periodTo) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT p FROM ProfitPosting p " +
                                    "JOIN FETCH p.account a " +
                                    "JOIN FETCH a.customer c " +
                                    "JOIN FETCH a.accountType at " +
                                    "JOIN FETCH p.schedule s " +
                                    "WHERE s.id = :scheduleId AND p.periodFrom = :periodFrom AND p.periodTo = :periodTo",
                            ProfitPosting.class
                    )
                    .setParameter("scheduleId", scheduleId)
                    .setParameter("periodFrom", periodFrom)
                    .setParameter("periodTo", periodTo)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Long countPostedThisMonth(LocalDate periodStart, LocalDate periodEnd) {
        return entityManager.createQuery(
                        "SELECT COUNT(p.id) FROM ProfitPosting p " +
                                "WHERE p.status = :posted " +
                                "AND p.postingDate >= :periodStart AND p.postingDate <= :periodEnd",
                        Long.class
                )
                .setParameter("posted", ProfitPostingStatus.POSTED)
                .setParameter("periodStart", periodStart)
                .setParameter("periodEnd", periodEnd)
                .getSingleResult();
    }

    public Long countByStatus(ProfitPostingStatus status) {
        return entityManager.createQuery(
                        "SELECT COUNT(p.id) FROM ProfitPosting p WHERE p.status = :status",
                        Long.class
                )
                .setParameter("status", status)
                .getSingleResult();
    }

    public List<ProfitPosting> findRecentFailed(int limit) {
        return entityManager.createQuery(
                        "SELECT p FROM ProfitPosting p " +
                                "JOIN FETCH p.account a " +
                                "JOIN FETCH a.customer c " +
                                "JOIN FETCH a.accountType at " +
                                "JOIN FETCH p.schedule s " +
                                "WHERE p.status = :failed " +
                                "ORDER BY p.id DESC",
                        ProfitPosting.class
                )
                .setParameter("failed", ProfitPostingStatus.FAILED)
                .setMaxResults(limit)
                .getResultList();
    }

    public String findLastPostingRef() {
        List<String> refs = entityManager.createQuery(
                        "SELECT p.postingRef FROM ProfitPosting p " +
                                "WHERE p.postingRef LIKE :prefix " +
                                "ORDER BY p.postingRef DESC",
                        String.class
                )
                .setParameter("prefix", "PRF-%")
                .setMaxResults(1)
                .getResultList();
        return refs.isEmpty() ? null : refs.get(0);
    }

    public Long countByAccountTypeId(Long accountTypeId) {
        return entityManager.createQuery(
                        "SELECT COUNT(p.id) FROM ProfitPosting p " +
                                "WHERE p.account.accountType.id = :accountTypeId",
                        Long.class
                )
                .setParameter("accountTypeId", accountTypeId)
                .getSingleResult();
    }
}
