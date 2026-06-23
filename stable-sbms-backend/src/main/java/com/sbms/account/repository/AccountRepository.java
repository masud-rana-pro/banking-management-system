package com.sbms.account.repository;

import com.sbms.account.entity.Account;
import com.sbms.account.enums.AccountStatus;
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
public class AccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Account save(Account entity) {
        entityManager.persist(entity);
        return entity;
    }

    public Account update(Account entity) {
        return entityManager.merge(entity);
    }

    public Optional<Account> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT a FROM Account a " +
                                    "JOIN FETCH a.customer c " +
                                    "JOIN FETCH a.accountType t " +
                                    "LEFT JOIN FETCH a.openingRequest r " +
                                    "WHERE a.id = :id",
                            Account.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Account> findAll() {
        return entityManager.createQuery(
                        "SELECT a FROM Account a " +
                                "JOIN FETCH a.customer c " +
                                "JOIN FETCH a.accountType t " +
                                "LEFT JOIN FETCH a.openingRequest r " +
                                "WHERE a.status <> :archived " +
                                "ORDER BY a.id DESC",
                        Account.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getResultList();
    }

    public List<Account> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }

        String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
        return entityManager.createQuery(
                        "SELECT a FROM Account a " +
                                "JOIN FETCH a.customer c " +
                                "JOIN FETCH a.accountType t " +
                                "LEFT JOIN FETCH a.openingRequest r " +
                                "WHERE a.status <> :archived AND (" +
                                "LOWER(a.accountNumber) LIKE :keyword OR " +
                                "LOWER(c.customerCode) LIKE :keyword OR " +
                                "LOWER(c.fullName) LIKE :keyword OR " +
                                "LOWER(t.typeCode) LIKE :keyword OR " +
                                "LOWER(t.typeName) LIKE :keyword OR " +
                                "LOWER(COALESCE(r.requestNo, '')) LIKE :keyword" +
                                ") ORDER BY a.id DESC",
                        Account.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("keyword", likeKeyword)
                .getResultList();
    }

    public Optional<Account> findByOpeningRequestId(Long openingRequestId) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT a FROM Account a " +
                                    "JOIN FETCH a.customer c " +
                                    "JOIN FETCH a.accountType t " +
                                    "LEFT JOIN FETCH a.openingRequest r " +
                                    "WHERE r.id = :openingRequestId AND a.status <> :archived",
                            Account.class
                    )
                    .setParameter("openingRequestId", openingRequestId)
                    .setParameter("archived", RecordStatus.ARCHIVED)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Account> findProfitEligibleAccounts() {
        return entityManager.createQuery(
                        "SELECT a FROM Account a " +
                                "JOIN FETCH a.customer c " +
                                "JOIN FETCH a.accountType t " +
                                "LEFT JOIN FETCH a.openingRequest r " +
                                "WHERE a.status = :active " +
                                "AND a.accountStatus = :accountStatus " +
                                "AND t.profitApplicable = true " +
                                "ORDER BY a.id ASC",
                        Account.class
                )
                .setParameter("active", RecordStatus.ACTIVE)
                .setParameter("accountStatus", AccountStatus.ACTIVE)
                .getResultList();
    }

    public List<Account> findByAccountTypeId(Long accountTypeId) {
        return entityManager.createQuery(
                        "SELECT a FROM Account a " +
                                "JOIN FETCH a.customer c " +
                                "JOIN FETCH a.accountType t " +
                                "LEFT JOIN FETCH a.openingRequest r " +
                                "WHERE t.id = :accountTypeId " +
                                "AND a.status <> :archived " +
                                "ORDER BY a.id DESC",
                        Account.class
                )
                .setParameter("accountTypeId", accountTypeId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getResultList();
    }

    public Long countAll() {
        return entityManager.createQuery(
                        "SELECT COUNT(a.id) FROM Account a WHERE a.status <> :archived",
                        Long.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public Long countByStatuses(List<AccountStatus> statuses) {
        return entityManager.createQuery(
                        "SELECT COUNT(a.id) FROM Account a WHERE a.accountStatus IN :statuses AND a.status <> :archived",
                        Long.class
                )
                .setParameter("statuses", statuses)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public String findLastAccountNumber() {
        List<String> result = entityManager.createQuery(
                        "SELECT a.accountNumber FROM Account a WHERE a.accountNumber LIKE :prefix ORDER BY a.accountNumber DESC",
                        String.class
                )
                .setParameter("prefix", "ACC-%")
                .setMaxResults(1)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }
}
