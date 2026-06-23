package com.sbms.transaction.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.transaction.entity.StandingInstruction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class StandingInstructionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public StandingInstruction save(StandingInstruction entity) {
        entityManager.persist(entity);
        return entity;
    }

    public List<StandingInstruction> findAll() {
        return entityManager.createQuery(
                        "SELECT s FROM StandingInstruction s " +
                                "WHERE s.status <> :archived ORDER BY s.id DESC",
                        StandingInstruction.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getResultList();
    }

    public String findLastInstructionCode() {
        List<String> codes = entityManager.createQuery(
                        "SELECT s.instructionCode FROM StandingInstruction s " +
                                "WHERE s.instructionCode LIKE :prefix ORDER BY s.instructionCode DESC",
                        String.class
                )
                .setParameter("prefix", "SI-%")
                .setMaxResults(1)
                .getResultList();
        return codes.isEmpty() ? null : codes.get(0);
    }
}
