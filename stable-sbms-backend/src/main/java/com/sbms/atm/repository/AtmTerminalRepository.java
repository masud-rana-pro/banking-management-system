package com.sbms.atm.repository;

import com.sbms.atm.entity.Terminal;
import com.sbms.atm.entity.TerminalCashBin;
import com.sbms.atm.entity.TerminalReconciliation;
import com.sbms.atm.entity.TerminalReplenishment;
import com.sbms.atm.enums.CashBinStatus;
import com.sbms.atm.enums.TerminalStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class AtmTerminalRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Terminal save(Terminal terminal) {
        entityManager.persist(terminal);
        return terminal;
    }

    public Terminal update(Terminal terminal) {
        return entityManager.merge(terminal);
    }

    public Optional<Terminal> findById(Long id) {
        List<Terminal> list = entityManager
                .createQuery("SELECT t FROM Terminal t WHERE t.id = :id", Terminal.class)
                .setParameter("id", id)
                .getResultList();

        return list.stream().findFirst();
    }

    public List<Terminal> findAll() {
        return entityManager
                .createQuery("SELECT t FROM Terminal t ORDER BY t.id DESC", Terminal.class)
                .getResultList();
    }

    public List<Terminal> findByStatuses(List<TerminalStatus> statuses) {
        return entityManager
                .createQuery("""
                        SELECT t
                        FROM Terminal t
                        WHERE t.status IN :statuses
                        ORDER BY t.id DESC
                        """, Terminal.class)
                .setParameter("statuses", statuses)
                .getResultList();
    }

    public boolean existsByTerminalCode(String terminalCode) {
        Long count = entityManager
                .createQuery("SELECT COUNT(t) FROM Terminal t WHERE LOWER(t.terminalCode) = LOWER(:terminalCode)", Long.class)
                .setParameter("terminalCode", terminalCode)
                .getSingleResult();

        return count > 0;
    }

    public boolean existsByTerminalCodeAndIdNot(String terminalCode, Long id) {
        Long count = entityManager
                .createQuery("""
                        SELECT COUNT(t)
                        FROM Terminal t
                        WHERE LOWER(t.terminalCode) = LOWER(:terminalCode)
                        AND t.id <> :id
                        """, Long.class)
                .setParameter("terminalCode", terminalCode)
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
    }

    public boolean branchExists(Long branchId) {
        Long count = entityManager
                .createQuery("SELECT COUNT(b) FROM Branch b WHERE b.id = :branchId", Long.class)
                .setParameter("branchId", branchId)
                .getSingleResult();

        return count > 0;
    }

    public List<Terminal> findDropdownActive() {
        return entityManager
                .createQuery("""
                        SELECT t
                        FROM Terminal t
                        WHERE t.status = :status
                        ORDER BY t.terminalCode ASC
                        """, Terminal.class)
                .setParameter("status", TerminalStatus.ACTIVE)
                .getResultList();
    }

    public long countByStatus(TerminalStatus status) {
        Long count = entityManager
                .createQuery("SELECT COUNT(t) FROM Terminal t WHERE t.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();

        return count == null ? 0L : count;
    }

    public long countCashBinsByStatus(CashBinStatus status) {
        Long count = entityManager
                .createQuery("SELECT COUNT(c) FROM TerminalCashBin c WHERE c.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();

        return count == null ? 0L : count;
    }

    public BigDecimalWrapper todayReplenishment(LocalDateWrapper date) {
        return new BigDecimalWrapper(
                entityManager
                        .createQuery("""
                                SELECT COALESCE(SUM(r.amountAdded), 0)
                                FROM TerminalReplenishment r
                                WHERE r.replenishmentDate = :date
                                """, java.math.BigDecimal.class)
                        .setParameter("date", date.value())
                        .getSingleResult(),
                entityManager
                        .createQuery("""
                                SELECT COUNT(r)
                                FROM TerminalReplenishment r
                                WHERE r.replenishmentDate = :date
                                """, Long.class)
                        .setParameter("date", date.value())
                        .getSingleResult()
        );
    }

    public List<TerminalCashBin> findAllCashBins() {
        return entityManager
                .createQuery("SELECT c FROM TerminalCashBin c ORDER BY c.id DESC", TerminalCashBin.class)
                .getResultList();
    }

    public List<TerminalReplenishment> findAllReplenishments() {
        return entityManager
                .createQuery("""
                        SELECT r
                        FROM TerminalReplenishment r
                        ORDER BY r.createdAt DESC, r.id DESC
                        """, TerminalReplenishment.class)
                .getResultList();
    }

    public List<TerminalReconciliation> findAllReconciliations() {
        return entityManager
                .createQuery("""
                        SELECT r
                        FROM TerminalReconciliation r
                        ORDER BY r.createdAt DESC, r.id DESC
                        """, TerminalReconciliation.class)
                .getResultList();
    }

    public record LocalDateWrapper(java.time.LocalDate value) {}

    public record BigDecimalWrapper(java.math.BigDecimal amount, Long count) {}
}
