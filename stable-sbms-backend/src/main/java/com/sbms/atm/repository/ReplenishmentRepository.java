package com.sbms.atm.repository;

import com.sbms.atm.entity.Terminal;
import com.sbms.atm.entity.TerminalCashBin;
import com.sbms.atm.entity.TerminalReplenishment;
import com.sbms.atm.enums.TerminalStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ReplenishmentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public TerminalReplenishment save(TerminalReplenishment replenishment) {
        entityManager.persist(replenishment);
        return replenishment;
    }

    public Optional<TerminalReplenishment> findById(Long id) {
        List<TerminalReplenishment> list = entityManager
                .createQuery("SELECT r FROM TerminalReplenishment r WHERE r.id = :id", TerminalReplenishment.class)
                .setParameter("id", id)
                .getResultList();

        return list.stream().findFirst();
    }

    public List<TerminalReplenishment> findAll() {
        return entityManager
                .createQuery("SELECT r FROM TerminalReplenishment r ORDER BY r.id DESC", TerminalReplenishment.class)
                .getResultList();
    }

    public List<TerminalReplenishment> findByTerminalId(Long terminalId) {
        return entityManager
                .createQuery("""
                        SELECT r
                        FROM TerminalReplenishment r
                        WHERE r.terminalId = :terminalId
                        ORDER BY r.id DESC
                        """, TerminalReplenishment.class)
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

    public Optional<TerminalCashBin> findCashBin(Long terminalId, String binNo) {
        List<TerminalCashBin> list = entityManager
                .createQuery("""
                        SELECT c
                        FROM TerminalCashBin c
                        WHERE c.terminalId = :terminalId
                        AND LOWER(c.binNo) = LOWER(:binNo)
                        """, TerminalCashBin.class)
                .setParameter("terminalId", terminalId)
                .setParameter("binNo", binNo)
                .getResultList();

        return list.stream().findFirst();
    }

    public TerminalCashBin updateCashBin(TerminalCashBin cashBin) {
        return entityManager.merge(cashBin);
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