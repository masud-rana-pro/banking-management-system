package com.sbms.account.repository;

import com.sbms.account.entity.AccountOpeningRequest;
import com.sbms.account.enums.AccountOpeningRequestStatus;
import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class AccountOpeningRequestRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public AccountOpeningRequest save(AccountOpeningRequest entity) {
        entityManager.persist(entity);
        return entity;
    }

    public AccountOpeningRequest update(AccountOpeningRequest entity) {
        return entityManager.merge(entity);
    }

    public Optional<AccountOpeningRequest> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT r FROM AccountOpeningRequest r " +
                                    "JOIN FETCH r.customer c " +
                                    "JOIN FETCH r.accountType t " +
                                    "WHERE r.id = :id",
                            AccountOpeningRequest.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<AccountOpeningRequest> findAll() {
        return entityManager.createQuery(
                        "SELECT r FROM AccountOpeningRequest r " +
                                "JOIN FETCH r.customer c " +
                                "JOIN FETCH r.accountType t " +
                                "WHERE r.status <> :archived " +
                                "ORDER BY r.id DESC",
                        AccountOpeningRequest.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getResultList();
    }

    public boolean existsByRequestNo(String requestNo) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(r.id) FROM AccountOpeningRequest r WHERE LOWER(r.requestNo) = :requestNo",
                        Long.class
                )
                .setParameter("requestNo", requestNo.toLowerCase())
                .getSingleResult();
        return count > 0;
    }

    public String findLastRequestNo() {
        List<String> result = entityManager.createQuery(
                        "SELECT r.requestNo FROM AccountOpeningRequest r " +
                                "WHERE r.requestNo LIKE :prefix ORDER BY r.requestNo DESC",
                        String.class
                )
                .setParameter("prefix", "AOR-%")
                .setMaxResults(1)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    public Long countByStatuses(List<AccountOpeningRequestStatus> statuses) {
        return entityManager.createQuery(
                        "SELECT COUNT(r.id) FROM AccountOpeningRequest r " +
                                "WHERE r.requestStatus IN :statuses AND r.status <> :archived",
                        Long.class
                )
                .setParameter("statuses", statuses)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }
}
