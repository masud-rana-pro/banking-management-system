package com.sbms.atm.repository;

import com.sbms.atm.entity.Terminal;
import com.sbms.atm.entity.TerminalCashBin;
import com.sbms.atm.enums.TerminalStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CashBinRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public TerminalCashBin save(TerminalCashBin cashBin) {
        entityManager.persist(cashBin);
        return cashBin;
    }

    public TerminalCashBin update(TerminalCashBin cashBin) {
        return entityManager.merge(cashBin);
    }

    public Optional<TerminalCashBin> findById(Long id) {
        List<TerminalCashBin> list = entityManager
                .createQuery("SELECT c FROM TerminalCashBin c WHERE c.id = :id", TerminalCashBin.class)
                .setParameter("id", id)
                .getResultList();

        return list.stream().findFirst();
    }

    public List<TerminalCashBin> findAll() {
        return entityManager
                .createQuery("SELECT c FROM TerminalCashBin c ORDER BY c.id DESC", TerminalCashBin.class)
                .getResultList();
    }

    public List<TerminalCashBin> findByTerminalId(Long terminalId) {
        return entityManager
                .createQuery("""
                        SELECT c
                        FROM TerminalCashBin c
                        WHERE c.terminalId = :terminalId
                        ORDER BY c.binNo ASC
                        """, TerminalCashBin.class)
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

    public boolean existsByTerminalIdAndBinNo(Long terminalId, String binNo) {
        Long count = entityManager
                .createQuery("""
                        SELECT COUNT(c)
                        FROM TerminalCashBin c
                        WHERE c.terminalId = :terminalId
                        AND LOWER(c.binNo) = LOWER(:binNo)
                        """, Long.class)
                .setParameter("terminalId", terminalId)
                .setParameter("binNo", binNo)
                .getSingleResult();

        return count > 0;
    }

    public boolean existsByTerminalIdAndBinNoAndIdNot(Long terminalId, String binNo, Long id) {
        Long count = entityManager
                .createQuery("""
                        SELECT COUNT(c)
                        FROM TerminalCashBin c
                        WHERE c.terminalId = :terminalId
                        AND LOWER(c.binNo) = LOWER(:binNo)
                        AND c.id <> :id
                        """, Long.class)
                .setParameter("terminalId", terminalId)
                .setParameter("binNo", binNo)
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
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