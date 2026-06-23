package com.sbms.kyc.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.kyc.entity.CustomerDocument;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CustomerDocumentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public CustomerDocument save(CustomerDocument document) {
        entityManager.persist(document);
        return document;
    }

    public CustomerDocument update(CustomerDocument document) {
        return entityManager.merge(document);
    }

    public Optional<CustomerDocument> findById(Long id) {
        try {
            CustomerDocument document = entityManager.createQuery(
                            "SELECT d FROM CustomerDocument d JOIN FETCH d.customer c WHERE d.id = :id",
                            CustomerDocument.class
                    )
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(document);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<CustomerDocument> findByCustomerId(Long customerId) {
        return entityManager.createQuery(
                        "SELECT d FROM CustomerDocument d JOIN FETCH d.customer c " +
                                "WHERE c.id = :customerId AND d.status <> :archived ORDER BY d.id DESC",
                        CustomerDocument.class
                )
                .setParameter("customerId", customerId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getResultList();
    }

    public Long countActiveByCustomerId(Long customerId) {
        return entityManager.createQuery(
                        "SELECT COUNT(d.id) FROM CustomerDocument d WHERE d.customer.id = :customerId AND d.status <> :archived",
                        Long.class
                )
                .setParameter("customerId", customerId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }
}
