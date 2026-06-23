package com.sbms.atm.repository;

import com.sbms.atm.entity.Terminal;
import com.sbms.atm.entity.TerminalReconciliation;
import com.sbms.atm.enums.TerminalStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ReconciliationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public TerminalReconciliation save(TerminalReconciliation reconciliation) {
        entityManager.persist(reconciliation);
        return reconciliation;
    }

    public Optional<TerminalReconciliation> findById(Long id) {
        List<TerminalReconciliation> list = entityManager
                .createQuery(
                        "SELECT r FROM TerminalReconciliation r WHERE r.id = :id",
                        TerminalReconciliation.class
                )
                .setParameter("id", id)
                .getResultList();

        return list.stream().findFirst();
    }

    public List<TerminalReconciliation> findAll() {
        return entityManager
                .createQuery(
                        "SELECT r FROM TerminalReconciliation r ORDER BY r.reconDate DESC, r.id DESC",
                        TerminalReconciliation.class
                )
                .getResultList();
    }

    public List<TerminalReconciliation> findByTerminalId(Long terminalId) {
        return entityManager
                .createQuery("""
                        SELECT r
                        FROM TerminalReconciliation r
                        WHERE r.terminalId = :terminalId
                        ORDER BY r.reconDate DESC, r.id DESC
                        """, TerminalReconciliation.class)
                .setParameter("terminalId", terminalId)
                .getResultList();
    }

    public Optional<Terminal> findTerminalById(Long terminalId) {
        List<Terminal> list = entityManager
                .createQuery("SELECT t FROM Terminal t WHERE t.id = :terminalId", Terminal.class)
                .setParameter("terminalId", terminalId)
                .getResultList();

        return list.stream().findFirst();
    }

    public String terminalCode(Long terminalId) {
        return entityManager
                .createQuery("SELECT t.terminalCode FROM Terminal t WHERE t.id = :terminalId", String.class)
                .setParameter("terminalId", terminalId)
                .getResultStream()
                .findFirst()
                .orElse("-");
    }

    public String terminalName(Long terminalId) {
        return entityManager
                .createQuery("SELECT t.terminalName FROM Terminal t WHERE t.id = :terminalId", String.class)
                .setParameter("terminalId", terminalId)
                .getResultStream()
                .findFirst()
                .orElse("-");
    }

    public boolean isTerminalActive(Terminal terminal) {
        return terminal != null && terminal.getStatus() == TerminalStatus.ACTIVE;
    }
}
