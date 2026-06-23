package com.sbms.branch.repository;

import com.sbms.branch.entity.Branch;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class BranchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Branch save(Branch branch) {
        if (branch.getId() == null) {
            entityManager.persist(branch);
            return branch;
        }
        return entityManager.merge(branch);
    }

    public Optional<Branch> findById(Long id) {
        List<Branch> list = entityManager
                .createQuery("from Branch b where b.id = :id", Branch.class)
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList();

        return list.stream().findFirst();
    }

    public List<Branch> findAll(String search, String status) {
        String hql = "from Branch b where 1=1";

        if (search != null && !search.isBlank()) {
            hql += " and (lower(b.branchCode) like :search " +
                    "or lower(b.branchName) like :search " +
                    "or lower(b.routingNo) like :search)";
        }

        if (status != null && !status.isBlank()) {
            hql += " and b.status = :status";
        }

        hql += " order by b.id desc";

        TypedQuery<Branch> query = entityManager.createQuery(hql, Branch.class);

        if (search != null && !search.isBlank()) {
            query.setParameter("search", "%" + search.toLowerCase() + "%");
        }

        if (status != null && !status.isBlank()) {
            query.setParameter("status", status);
        }

        return query.getResultList();
    }

    public boolean existsByBranchCode(String branchCode, Long exceptId) {
        String hql = "select count(b.id) from Branch b " +
                "where lower(b.branchCode) = :branchCode " +
                "and (:exceptId is null or b.id <> :exceptId)";

        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("branchCode", branchCode.toLowerCase())
                .setParameter("exceptId", exceptId)
                .getSingleResult();

        return count > 0;
    }
    
    public Long countAllActiveOrInactive() {
        String hql = "select count(b.id) from Branch b where b.isDeleted = false";

        return entityManager.createQuery(hql, Long.class)
                .getSingleResult();
    }

    public Long countByStatus(String status) {
        String hql = "select count(b.id) from Branch b " +
                "where b.isDeleted = false and b.status = :status";

        return entityManager.createQuery(hql, Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }

    public boolean existsByRoutingNo(String routingNo, Long exceptId) {
        String hql = "select count(b.id) from Branch b " +
                "where b.routingNo = :routingNo " +
                "and (:exceptId is null or b.id <> :exceptId)";

        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("routingNo", routingNo)
                .setParameter("exceptId", exceptId)
                .getSingleResult();

        return count > 0;
    }

    public void softDelete(Long id, Long userId, String reason) {
        entityManager.createQuery(
                        "update Branch b set " +
                                "b.isDeleted = false, " +
                                "b.deletedAt = :deletedAt, " +
                                "b.deletedBy = :deletedBy, " +
                                "b.deleteReason = :reason, " +
                                "b.status = 'INACTIVE' " +
                                "where b.id = :id"
                )
                .setParameter("deletedAt", LocalDateTime.now())
                .setParameter("deletedBy", userId)
                .setParameter("reason", reason)
                .setParameter("id", id)
                .executeUpdate();
    }

    public void restore(Long id) {
        entityManager.createQuery(
                        "update Branch b set " +
                                "b.isDeleted = false, " +
                                "b.deletedAt = null, " +
                                "b.deletedBy = null, " +
                                "b.deleteReason = null, " +
                                "b.status = 'ACTIVE' " +
                                "where b.id = :id"
                )
                .setParameter("id", id)
                .executeUpdate();
    }
}