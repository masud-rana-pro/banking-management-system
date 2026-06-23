package com.sbms.customer.repository;

import com.sbms.customer.entity.CustomerIdentity;
import com.sbms.customer.enums.DocumentType;
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
public class CustomerIdentityRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public CustomerIdentity save(CustomerIdentity identity) {
        entityManager.persist(identity);
        return identity;
    }

    public CustomerIdentity update(CustomerIdentity identity) {
        return entityManager.merge(identity);
    }

    public Optional<CustomerIdentity> findById(Long id) {
        try {
            CustomerIdentity identity = entityManager.createQuery(
                            "SELECT i FROM CustomerIdentity i " +
                                    "JOIN FETCH i.customer c " +
                                    "WHERE i.id = :id",
                            CustomerIdentity.class
                    )
                    .setParameter("id", id)
                    .getSingleResult();

            return Optional.of(identity);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<CustomerIdentity> findActiveById(Long id) {
        try {
            CustomerIdentity identity = entityManager.createQuery(
                            "SELECT i FROM CustomerIdentity i " +
                                    "JOIN FETCH i.customer c " +
                                    "WHERE i.id = :id AND i.status <> :archived",
                            CustomerIdentity.class
                    )
                    .setParameter("id", id)
                    .setParameter("archived", RecordStatus.ARCHIVED)
                    .getSingleResult();

            return Optional.of(identity);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<CustomerIdentity> findByCustomerId(Long customerId) {
        return entityManager.createQuery(
                        "SELECT i FROM CustomerIdentity i " +
                                "JOIN FETCH i.customer c " +
                                "WHERE c.id = :customerId " +
                                "AND i.status <> :archived " +
                                "ORDER BY i.id DESC",
                        CustomerIdentity.class
                )
                .setParameter("customerId", customerId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getResultList();
    }

    public List<CustomerIdentity> findAll() {
        return entityManager.createQuery(
                        "SELECT i FROM CustomerIdentity i " +
                                "JOIN FETCH i.customer c " +
                                "ORDER BY i.id DESC",
                        CustomerIdentity.class
                )
                .getResultList();
    }

    public boolean existsByDocument(DocumentType documentType, String documentNo) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(i.id) FROM CustomerIdentity i " +
                                "WHERE i.documentType = :documentType " +
                                "AND LOWER(i.documentNo) = :documentNo " +
                                "AND i.status <> :archived",
                        Long.class
                )
                .setParameter("documentType", documentType)
                .setParameter("documentNo", documentNo.trim().toLowerCase())
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();

        return count > 0;
    }

    public boolean existsByDocumentExceptId(DocumentType documentType, String documentNo, Long id) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(i.id) FROM CustomerIdentity i " +
                                "WHERE i.documentType = :documentType " +
                                "AND LOWER(i.documentNo) = :documentNo " +
                                "AND i.id <> :id " +
                                "AND i.status <> :archived",
                        Long.class
                )
                .setParameter("documentType", documentType)
                .setParameter("documentNo", documentNo.trim().toLowerCase())
                .setParameter("id", id)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();

        return count > 0;
    }

    public Long countActiveIdentityByCustomer(Long customerId) {
        return entityManager.createQuery(
                        "SELECT COUNT(i.id) FROM CustomerIdentity i " +
                                "WHERE i.customer.id = :customerId " +
                                "AND i.status <> :archived",
                        Long.class
                )
                .setParameter("customerId", customerId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public Long countVerifiedIdentityByCustomer(Long customerId) {
        return entityManager.createQuery(
                        "SELECT COUNT(i.id) FROM CustomerIdentity i " +
                                "WHERE i.customer.id = :customerId " +
                                "AND i.verifiedFlag = true " +
                                "AND i.status <> :archived",
                        Long.class
                )
                .setParameter("customerId", customerId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }
}