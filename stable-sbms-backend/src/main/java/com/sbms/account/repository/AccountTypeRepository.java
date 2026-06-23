package com.sbms.account.repository;

import com.sbms.account.entity.AccountType;
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
public class AccountTypeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public AccountType save(AccountType entity) {
        entityManager.persist(entity);
        return entity;
    }

    public AccountType update(AccountType entity) {
        return entityManager.merge(entity);
    }

    public Optional<AccountType> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT a FROM AccountType a WHERE a.id = :id",
                            AccountType.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<AccountType> findAll() {
        return entityManager.createQuery(
                        "SELECT a FROM AccountType a ORDER BY a.id DESC",
                        AccountType.class
                )
                .getResultList();
    }

    public List<AccountType> findDropdown() {
        return entityManager.createQuery(
                        "SELECT a FROM AccountType a WHERE a.status = :active ORDER BY a.typeName ASC",
                        AccountType.class
                )
                .setParameter("active", RecordStatus.ACTIVE)
                .getResultList();
    }

    public boolean existsByTypeCode(String typeCode) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(a.id) FROM AccountType a WHERE LOWER(a.typeCode) = :typeCode",
                        Long.class
                )
                .setParameter("typeCode", typeCode.toLowerCase())
                .getSingleResult();
        return count > 0;
    }

    public boolean existsByTypeCodeExceptId(String typeCode, Long id) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(a.id) FROM AccountType a WHERE LOWER(a.typeCode) = :typeCode AND a.id <> :id",
                        Long.class
                )
                .setParameter("typeCode", typeCode.toLowerCase())
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }

    public String findLastTypeCode() {
        List<String> result = entityManager.createQuery(
                        "SELECT a.typeCode FROM AccountType a WHERE a.typeCode LIKE :prefix ORDER BY a.typeCode DESC",
                        String.class
                )
                .setParameter("prefix", "ACT-%")
                .setMaxResults(1)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }
}
