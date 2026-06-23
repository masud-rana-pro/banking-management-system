package com.sbms.branch.repository;

import com.sbms.branch.entity.BranchUserAssignment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class BranchUserAssignmentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public BranchUserAssignment save(BranchUserAssignment entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return entity;
        }
        return entityManager.merge(entity);
    }

    public Optional<BranchUserAssignment> findById(Long id) {
        List<BranchUserAssignment> list = entityManager
                .createQuery("from BranchUserAssignment a where a.id = :id", BranchUserAssignment.class)
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList();

        return list.stream().findFirst();
    }

    public List<BranchUserAssignment> findAll(Long branchId, Long userId, String status) {
        String hql = "from BranchUserAssignment a where 1=1";

        if (branchId != null) hql += " and a.branchId = :branchId";
        if (userId != null) hql += " and a.userId = :userId";
        if (status != null && !status.isBlank()) hql += " and a.status = :status";

        hql += " order by a.id desc";

        TypedQuery<BranchUserAssignment> query =
                entityManager.createQuery(hql, BranchUserAssignment.class);

        if (branchId != null) query.setParameter("branchId", branchId);
        if (userId != null) query.setParameter("userId", userId);
        if (status != null && !status.isBlank()) query.setParameter("status", status);

        return query.getResultList();
    }

    
    public boolean existsActivePrimary(Long branchId, String assignmentRole, Long excludeId) {
        String hql = "select count(a.id) from BranchUserAssignment a " +
                "where a.branchId = :branchId " +
                "and a.assignmentRole = :assignmentRole " +
                "and a.isPrimary = true " +
                "and a.status = 'ACTIVE' " +
                "and (:excludeId is null or a.id <> :excludeId)";

        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("branchId", branchId)
                .setParameter("assignmentRole", assignmentRole)
                .setParameter("excludeId", excludeId)
                .getSingleResult();

        return count > 0;
    }

    public Long countPendingAssignments() {
        String hql = "select count(a.id) from BranchUserAssignment a " +
                "where a.status = 'PENDING'";

        return entityManager.createQuery(hql, Long.class)
                .getSingleResult();
    }
    
    public boolean existsActiveAssignment(Long branchId, Long userId, Long excludeId) {
        String hql = "select count(a.id) from BranchUserAssignment a " +
                "where a.branchId = :branchId " +
                "and a.userId = :userId " +
                "and a.status = 'ACTIVE' " +
                "and (:excludeId is null or a.id <> :excludeId)";

        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("branchId", branchId)
                .setParameter("userId", userId)
                .setParameter("excludeId", excludeId)
                .getSingleResult();

        return count > 0;
    }

    
    public boolean existsActiveTellerAssignment(Long branchId, Long userId) {
        String hql = "select count(a.id) from BranchUserAssignment a " +
                "where a.branchId = :branchId " +
                "and a.userId = :userId " +
                "and a.status = 'ACTIVE' " +
                "and a.assignmentRole = 'TELLER'";

        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("branchId", branchId)
                .setParameter("userId", userId)
                .getSingleResult();

        return count > 0;
    }

    public void deactivate(Long id) {
        entityManager.createQuery(
                "update BranchUserAssignment a set a.status = 'INACTIVE' where a.id = :id"
        ).setParameter("id", id).executeUpdate();
    }
}