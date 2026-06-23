package com.sbms.shariah.repository;

import com.sbms.shariah.entity.ShariahReviewDecision;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ShariahReviewDecisionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ShariahReviewDecision save(ShariahReviewDecision entity) {
        entityManager.persist(entity);
        return entity;
    }

    public List<ShariahReviewDecision> findByCaseId(Long caseId) {
        return entityManager.createQuery(
                "SELECT d FROM ShariahReviewDecision d " +
                        "JOIN FETCH d.reviewCase c " +
                        "WHERE c.id = :caseId ORDER BY d.decisionAt DESC, d.id DESC",
                ShariahReviewDecision.class
        ).setParameter("caseId", caseId).getResultList();
    }

    public List<ShariahReviewDecision> findLatest(int limit) {
        return entityManager.createQuery(
                "SELECT d FROM ShariahReviewDecision d " +
                        "JOIN FETCH d.reviewCase c " +
                        "ORDER BY d.decisionAt DESC, d.id DESC",
                ShariahReviewDecision.class
        ).setMaxResults(limit).getResultList();
    }
}
