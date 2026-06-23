package com.sbms.transaction.repository;

import com.sbms.transaction.entity.FundTransfer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class FundTransferRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public FundTransfer save(FundTransfer entity) {
        entityManager.persist(entity);
        return entity;
    }

    public Optional<FundTransfer> findByTransactionId(Long transactionId) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT f FROM FundTransfer f WHERE f.transaction.id = :transactionId",
                            FundTransfer.class
                    )
                    .setParameter("transactionId", transactionId)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
