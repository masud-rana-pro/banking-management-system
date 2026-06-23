package com.sbms.shariah.repository;

import com.sbms.shariah.entity.ShariahReviewChecklist;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ShariahReviewChecklistRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ShariahReviewChecklist save(ShariahReviewChecklist entity) {
        entityManager.persist(entity);
        return entity;
    }

    public List<ShariahReviewChecklist> findByCaseId(Long caseId) {
        return entityManager.createQuery(
                "SELECT c FROM ShariahReviewChecklist c " +
                        "JOIN FETCH c.reviewCase rc " +
                        "JOIN FETCH c.checklistItem i " +
                        "WHERE rc.id = :caseId ORDER BY i.itemCode ASC",
                ShariahReviewChecklist.class
        ).setParameter("caseId", caseId).getResultList();
    }

    public void deleteByCaseId(Long caseId) {
        entityManager.createQuery(
                "DELETE FROM ShariahReviewChecklist c WHERE c.reviewCase.id = :caseId"
        ).setParameter("caseId", caseId).executeUpdate();
    }
}
