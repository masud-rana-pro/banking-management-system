package com.sbms.customer.repository;

import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.CustomerStatus;
import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CustomerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Customer save(Customer customer) {
        entityManager.persist(customer);
        return customer;
    }

    public Customer update(Customer customer) {
        return entityManager.merge(customer);
    }

    public Optional<Customer> findById(Long id) {
        try {
            Customer customer = entityManager.createQuery(
                            "SELECT c FROM Customer c WHERE c.id = :id",
                            Customer.class
                    )
                    .setParameter("id", id)
                    .getSingleResult();

            return Optional.of(customer);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Customer> findActiveById(Long id) {
        try {
            Customer customer = entityManager.createQuery(
                            "SELECT c FROM Customer c WHERE c.id = :id AND c.status <> :archived",
                            Customer.class
                    )
                    .setParameter("id", id)
                    .setParameter("archived", RecordStatus.ARCHIVED)
                    .getSingleResult();

            return Optional.of(customer);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Customer> findAll() {
        return entityManager.createQuery(
                        "SELECT c FROM Customer c ORDER BY c.id DESC",
                        Customer.class
                )
                .getResultList();
    }

    public List<Customer> findAllActive() {
        return entityManager.createQuery(
                        "SELECT c FROM Customer c WHERE c.status <> :archived ORDER BY c.id DESC",
                        Customer.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getResultList();
    }

    public List<Customer> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllActive();
        }

        String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";

        return entityManager.createQuery(
                        "SELECT c FROM Customer c " +
                                "WHERE c.status <> :archived " +
                                "AND (" +
                                "LOWER(c.customerCode) LIKE :keyword OR " +
                                "LOWER(c.fullName) LIKE :keyword OR " +
                                "LOWER(c.mobile) LIKE :keyword OR " +
                                "LOWER(c.email) LIKE :keyword" +
                                ") " +
                                "ORDER BY c.id DESC",
                        Customer.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("keyword", likeKeyword)
                .getResultList();
    }

    public List<Customer> findDropdownCustomers(String keyword) {
        StringBuilder hql = new StringBuilder(
                "SELECT c FROM Customer c WHERE c.status = :active "
        );

        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("AND (LOWER(c.customerCode) LIKE :keyword ")
                    .append("OR LOWER(c.fullName) LIKE :keyword ")
                    .append("OR LOWER(c.mobile) LIKE :keyword) ");
        }

        hql.append("ORDER BY c.fullName ASC");

        TypedQuery<Customer> query = entityManager.createQuery(hql.toString(), Customer.class);
        query.setParameter("active", RecordStatus.ACTIVE);

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }

        query.setMaxResults(50);
        return query.getResultList();
    }

    public boolean existsByCustomerCode(String customerCode) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Customer c WHERE c.customerCode = :customerCode",
                        Long.class
                )
                .setParameter("customerCode", customerCode)
                .getSingleResult();

        return count > 0;
    }

    public boolean existsByMobile(String mobile) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Customer c WHERE c.mobile = :mobile",
                        Long.class
                )
                .setParameter("mobile", mobile)
                .getSingleResult();

        return count > 0;
    }

    public boolean existsByMobileExceptId(String mobile, Long id) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Customer c WHERE c.mobile = :mobile AND c.id <> :id",
                        Long.class
                )
                .setParameter("mobile", mobile)
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
    }

    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        Long count = entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Customer c WHERE LOWER(c.email) = :email",
                        Long.class
                )
                .setParameter("email", email.trim().toLowerCase())
                .getSingleResult();

        return count > 0;
    }

    public boolean existsByEmailExceptId(String email, Long id) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        Long count = entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Customer c WHERE LOWER(c.email) = :email AND c.id <> :id",
                        Long.class
                )
                .setParameter("email", email.trim().toLowerCase())
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
    }

    public String findLastCustomerCode() {
        List<String> codes = entityManager.createQuery(
                        "SELECT c.customerCode FROM Customer c " +
                                "WHERE c.customerCode LIKE :prefix " +
                                "ORDER BY c.customerCode DESC",
                        String.class
                )
                .setParameter("prefix", "CUS-%")
                .setMaxResults(1)
                .getResultList();

        return codes.isEmpty() ? null : codes.get(0);
    }

    public Long countAllNonArchived() {
        return entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Customer c WHERE c.status <> :archived",
                        Long.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public Optional<Customer> findByMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT c FROM Customer c WHERE c.mobile = :mobile",
                            Customer.class
                    )
                    .setParameter("mobile", mobile.trim())
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<Customer> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT c FROM Customer c WHERE LOWER(c.email) = :email",
                            Customer.class
                    )
                    .setParameter("email", email.trim().toLowerCase())
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Long countByCustomerStatus(CustomerStatus customerStatus) {
        return entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Customer c " +
                                "WHERE c.customerStatus = :customerStatus " +
                                "AND c.status <> :archived",
                        Long.class
                )
                .setParameter("customerStatus", customerStatus)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public Long countByRecordStatus(RecordStatus status) {
        return entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Customer c WHERE c.status = :status",
                        Long.class
                )
                .setParameter("status", status)
                .getSingleResult();
    }

    public Long countNewCustomersThisMonth() {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.plusMonths(1).atDay(1).atStartOfDay();

        return entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Customer c " +
                                "WHERE c.createdAt >= :startDate " +
                                "AND c.createdAt < :endDate " +
                                "AND c.status <> :archived",
                        Long.class
                )
                .setParameter("startDate", start)
                .setParameter("endDate", end)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public Long countIncompleteProfiles() {
        return entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Customer c " +
                                "WHERE c.status <> :archived " +
                                "AND (" +
                                "c.fullName IS NULL OR c.fullName = '' OR " +
                                "c.mobile IS NULL OR c.mobile = '' OR " +
                                "c.branchId IS NULL OR c.branchId <= 0 OR " +
                                "c.customerType IS NULL OR " +
                                "NOT EXISTS (" +
                                "SELECT a.id FROM CustomerAddress a " +
                                "WHERE a.customer.id = c.id AND a.status <> :archived" +
                                ") OR " +
                                "NOT EXISTS (" +
                                "SELECT i.id FROM CustomerIdentity i " +
                                "WHERE i.customer.id = c.id AND i.status <> :archived" +
                                ")" +
                                ")",
                        Long.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }
}
