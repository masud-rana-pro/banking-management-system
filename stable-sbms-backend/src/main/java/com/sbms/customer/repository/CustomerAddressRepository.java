package com.sbms.customer.repository;

import com.sbms.customer.entity.CustomerAddress;
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
public class CustomerAddressRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public CustomerAddress save(CustomerAddress address) {
        entityManager.persist(address);
        return address;
    }

    public CustomerAddress update(CustomerAddress address) {
        return entityManager.merge(address);
    }

    public Optional<CustomerAddress> findById(Long id) {
        try {
            CustomerAddress address = entityManager.createQuery(
                            "SELECT a FROM CustomerAddress a " +
                                    "JOIN FETCH a.customer c " +
                                    "WHERE a.id = :id",
                            CustomerAddress.class
                    )
                    .setParameter("id", id)
                    .getSingleResult();

            return Optional.of(address);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<CustomerAddress> findActiveById(Long id) {
        try {
            CustomerAddress address = entityManager.createQuery(
                            "SELECT a FROM CustomerAddress a " +
                                    "JOIN FETCH a.customer c " +
                                    "WHERE a.id = :id AND a.status <> :archived",
                            CustomerAddress.class
                    )
                    .setParameter("id", id)
                    .setParameter("archived", RecordStatus.ARCHIVED)
                    .getSingleResult();

            return Optional.of(address);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<CustomerAddress> findByCustomerId(Long customerId) {
        return entityManager.createQuery(
                        "SELECT a FROM CustomerAddress a " +
                                "JOIN FETCH a.customer c " +
                                "WHERE c.id = :customerId " +
                                "AND a.status <> :archived " +
                                "ORDER BY a.primaryAddress DESC, a.id DESC",
                        CustomerAddress.class
                )
                .setParameter("customerId", customerId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getResultList();
    }

    public List<CustomerAddress> findAll() {
        return entityManager.createQuery(
                        "SELECT a FROM CustomerAddress a " +
                                "JOIN FETCH a.customer c " +
                                "ORDER BY a.id DESC",
                        CustomerAddress.class
                )
                .getResultList();
    }

    public void clearPrimaryAddress(Long customerId) {
        entityManager.createQuery(
                        "UPDATE CustomerAddress a " +
                                "SET a.primaryAddress = false " +
                                "WHERE a.customer.id = :customerId"
                )
                .setParameter("customerId", customerId)
                .executeUpdate();
    }

    public boolean existsPrimaryAddress(Long customerId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(a.id) FROM CustomerAddress a " +
                                "WHERE a.customer.id = :customerId " +
                                "AND a.primaryAddress = true " +
                                "AND a.status <> :archived",
                        Long.class
                )
                .setParameter("customerId", customerId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();

        return count > 0;
    }

    public Long countActiveAddressByCustomer(Long customerId) {
        return entityManager.createQuery(
                        "SELECT COUNT(a.id) FROM CustomerAddress a " +
                                "WHERE a.customer.id = :customerId " +
                                "AND a.status <> :archived",
                        Long.class
                )
                .setParameter("customerId", customerId)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }
}