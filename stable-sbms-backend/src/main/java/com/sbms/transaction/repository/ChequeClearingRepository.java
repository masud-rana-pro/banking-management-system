package com.sbms.transaction.repository;

import com.sbms.transaction.entity.ChequeClearing;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class ChequeClearingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ChequeClearing save(ChequeClearing entity) {
        entityManager.persist(entity);
        return entity;
    }

    public Optional<ChequeClearing> findByTransactionId(Long transactionId) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT c FROM ChequeClearing c WHERE c.transaction.id = :transactionId",
                            ChequeClearing.class
                    )
                    .setParameter("transactionId", transactionId)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
