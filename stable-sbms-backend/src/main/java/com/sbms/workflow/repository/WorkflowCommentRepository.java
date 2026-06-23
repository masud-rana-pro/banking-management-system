package com.sbms.workflow.repository;

import com.sbms.workflow.entity.WorkflowComment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class WorkflowCommentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public WorkflowComment save(WorkflowComment entity) {
        entityManager.persist(entity);
        return entity;
    }

    public List<WorkflowComment> findByModuleReference(String moduleName, Long referenceId) {
        return entityManager.createQuery(
                        "SELECT c FROM WorkflowComment c WHERE UPPER(c.moduleName) = :moduleName AND c.referenceId = :referenceId ORDER BY c.commentAt ASC, c.id ASC",
                        WorkflowComment.class
                )
                .setParameter("moduleName", moduleName == null ? "" : moduleName.trim().toUpperCase())
                .setParameter("referenceId", referenceId)
                .getResultList();
    }
}
