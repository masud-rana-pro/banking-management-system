package com.sbms.statement.repository;

import com.sbms.atm.entity.Terminal;
import com.sbms.atm.entity.TerminalReconciliation;
import com.sbms.atm.entity.TerminalReplenishment;
import com.sbms.branch.entity.BranchCashLedger;
import com.sbms.branch.entity.VaultBalance;
import com.sbms.profit.entity.ProfitPosting;
import com.sbms.profit.enums.ProfitPostingStatus;
import com.sbms.transaction.entity.TransactionJournal;
import com.sbms.transaction.enums.TransactionStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class StatementDataRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<TransactionJournal> findTransactionsByAccount(Long accountId, LocalDate fromDate, LocalDate toDate) {
        return entityManager.createQuery(
                        "SELECT t FROM TransactionJournal t " +
                                "LEFT JOIN FETCH t.debitAccount da " +
                                "LEFT JOIN FETCH t.creditAccount ca " +
                                "WHERE (da.id = :accountId OR ca.id = :accountId) " +
                                "AND t.transactionDate >= :fromTime AND t.transactionDate <= :toTime " +
                                "AND t.transactionStatus = :posted " +
                                "ORDER BY t.transactionDate ASC, t.id ASC",
                        TransactionJournal.class
                )
                .setParameter("accountId", accountId)
                .setParameter("fromTime", fromDate.atStartOfDay())
                .setParameter("toTime", toDate.atTime(23, 59, 59))
                .setParameter("posted", TransactionStatus.POSTED)
                .getResultList();
    }

    public List<TransactionJournal> findTransactionsByBranch(Long branchId, LocalDate fromDate, LocalDate toDate) {
        return entityManager.createQuery(
                        "SELECT t FROM TransactionJournal t " +
                                "LEFT JOIN FETCH t.debitAccount da " +
                                "LEFT JOIN FETCH t.creditAccount ca " +
                                "WHERE t.branchId = :branchId " +
                                "AND t.transactionDate >= :fromTime AND t.transactionDate <= :toTime " +
                                "AND t.transactionStatus = :posted " +
                                "ORDER BY t.transactionDate ASC, t.id ASC",
                        TransactionJournal.class
                )
                .setParameter("branchId", branchId)
                .setParameter("fromTime", fromDate.atStartOfDay())
                .setParameter("toTime", toDate.atTime(23, 59, 59))
                .setParameter("posted", TransactionStatus.POSTED)
                .getResultList();
    }

    public List<ProfitPosting> findProfitPostingsByAccount(Long accountId, LocalDate fromDate, LocalDate toDate) {
        return entityManager.createQuery(
                        "SELECT p FROM ProfitPosting p " +
                                "JOIN FETCH p.account a " +
                                "WHERE a.id = :accountId " +
                                "AND p.postingDate >= :fromDate AND p.postingDate <= :toDate " +
                                "AND p.status = :posted " +
                                "ORDER BY p.postingDate ASC, p.id ASC",
                        ProfitPosting.class
                )
                .setParameter("accountId", accountId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .setParameter("posted", ProfitPostingStatus.POSTED)
                .getResultList();
    }

    public List<VaultBalance> findVaultBalancesByBranch(Long branchId, LocalDate fromDate, LocalDate toDate) {
        return entityManager.createQuery(
                        "SELECT v FROM VaultBalance v " +
                                "WHERE v.branchId = :branchId " +
                                "AND v.balanceDate >= :fromDate AND v.balanceDate <= :toDate " +
                                "ORDER BY v.balanceDate ASC, v.id ASC",
                        VaultBalance.class
                )
                .setParameter("branchId", branchId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList();
    }

    public List<BranchCashLedger> findCashLedgerByBranch(Long branchId, LocalDate fromDate, LocalDate toDate) {
        return entityManager.createQuery(
                        "SELECT l FROM BranchCashLedger l " +
                                "WHERE l.branchId = :branchId " +
                                "AND l.ledgerDate >= :fromDate AND l.ledgerDate <= :toDate " +
                                "ORDER BY l.ledgerDate ASC, l.id ASC",
                        BranchCashLedger.class
                )
                .setParameter("branchId", branchId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList();
    }

    public List<Terminal> findActiveTerminalsByBranch(Long branchId) {
        return entityManager.createQuery(
                        "SELECT t FROM Terminal t WHERE t.branchId = :branchId ORDER BY t.id ASC",
                        Terminal.class
                )
                .setParameter("branchId", branchId)
                .getResultList();
    }

    public List<TerminalReplenishment> findReplenishmentsByBranch(Long branchId, LocalDate fromDate, LocalDate toDate) {
        return entityManager.createQuery(
                        "SELECT r FROM TerminalReplenishment r " +
                                "WHERE r.terminalId IN (SELECT t.id FROM Terminal t WHERE t.branchId = :branchId) " +
                                "AND r.replenishmentDate >= :fromDate AND r.replenishmentDate <= :toDate " +
                                "ORDER BY r.replenishmentDate ASC, r.id ASC",
                        TerminalReplenishment.class
                )
                .setParameter("branchId", branchId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList();
    }

    public List<TerminalReconciliation> findReconciliationsByBranch(Long branchId, LocalDate fromDate, LocalDate toDate) {
        return entityManager.createQuery(
                        "SELECT r FROM TerminalReconciliation r " +
                                "WHERE r.terminalId IN (SELECT t.id FROM Terminal t WHERE t.branchId = :branchId) " +
                                "AND r.reconDate >= :fromDate AND r.reconDate <= :toDate " +
                                "ORDER BY r.reconDate ASC, r.id ASC",
                        TerminalReconciliation.class
                )
                .setParameter("branchId", branchId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList();
    }

    public BigDecimal sumProfitByAccount(Long accountId, LocalDate fromDate, LocalDate toDate) {
        BigDecimal value = entityManager.createQuery(
                        "SELECT COALESCE(SUM(p.profitAmount), 0) FROM ProfitPosting p " +
                                "WHERE p.account.id = :accountId " +
                                "AND p.postingDate >= :fromDate AND p.postingDate <= :toDate " +
                                "AND p.status = :posted",
                        BigDecimal.class
                )
                .setParameter("accountId", accountId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .setParameter("posted", ProfitPostingStatus.POSTED)
                .getSingleResult();
        return value == null ? BigDecimal.ZERO : value;
    }

    public Long countTransactionsByBranch(Long branchId, LocalDate fromDate, LocalDate toDate) {
        return entityManager.createQuery(
                        "SELECT COUNT(t.id) FROM TransactionJournal t " +
                                "WHERE t.branchId = :branchId " +
                                "AND t.transactionDate >= :fromTime AND t.transactionDate <= :toTime",
                        Long.class
                )
                .setParameter("branchId", branchId)
                .setParameter("fromTime", fromDate.atStartOfDay())
                .setParameter("toTime", toDate.atTime(23, 59, 59))
                .getSingleResult();
    }

    public BigDecimal sumTransactionAmountByBranch(Long branchId, LocalDate fromDate, LocalDate toDate) {
        BigDecimal value = entityManager.createQuery(
                        "SELECT COALESCE(SUM(t.amount), 0) FROM TransactionJournal t " +
                                "WHERE t.branchId = :branchId " +
                                "AND t.transactionDate >= :fromTime AND t.transactionDate <= :toTime",
                        BigDecimal.class
                )
                .setParameter("branchId", branchId)
                .setParameter("fromTime", fromDate.atStartOfDay())
                .setParameter("toTime", toDate.atTime(23, 59, 59))
                .getSingleResult();
        return value == null ? BigDecimal.ZERO : value;
    }

    public BigDecimal sumCashInByBranch(Long branchId, LocalDate fromDate, LocalDate toDate) {
        BigDecimal value = entityManager.createQuery(
                        "SELECT COALESCE(SUM(l.creditAmount), 0) FROM BranchCashLedger l " +
                                "WHERE l.branchId = :branchId " +
                                "AND l.ledgerDate >= :fromDate AND l.ledgerDate <= :toDate",
                        BigDecimal.class
                )
                .setParameter("branchId", branchId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getSingleResult();
        return value == null ? BigDecimal.ZERO : value;
    }

    public BigDecimal sumCashOutByBranch(Long branchId, LocalDate fromDate, LocalDate toDate) {
        BigDecimal value = entityManager.createQuery(
                        "SELECT COALESCE(SUM(l.debitAmount), 0) FROM BranchCashLedger l " +
                                "WHERE l.branchId = :branchId " +
                                "AND l.ledgerDate >= :fromDate AND l.ledgerDate <= :toDate",
                        BigDecimal.class
                )
                .setParameter("branchId", branchId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getSingleResult();
        return value == null ? BigDecimal.ZERO : value;
    }

    public BigDecimal latestVaultClosingBalance(Long branchId, LocalDate fromDate, LocalDate toDate) {
        List<BigDecimal> values = entityManager.createQuery(
                        "SELECT v.closingBalance FROM VaultBalance v " +
                                "WHERE v.branchId = :branchId " +
                                "AND v.balanceDate >= :fromDate AND v.balanceDate <= :toDate " +
                                "ORDER BY v.balanceDate DESC, v.id DESC",
                        BigDecimal.class
                )
                .setParameter("branchId", branchId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .setMaxResults(1)
                .getResultList();
        return values.isEmpty() ? BigDecimal.ZERO : values.get(0);
    }

    public Long countActiveAccountsByBranch(Long branchId) {
        return entityManager.createQuery(
                        "SELECT COUNT(a.id) FROM Account a " +
                                "WHERE a.branchId = :branchId AND a.status <> :archived",
                        Long.class
                )
                .setParameter("branchId", branchId)
                .setParameter("archived", com.sbms.customer.enums.RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public Long countCustomersByBranch(Long branchId) {
        return entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Customer c WHERE c.branchId = :branchId AND c.status <> :archived",
                        Long.class
                )
                .setParameter("branchId", branchId)
                .setParameter("archived", com.sbms.customer.enums.RecordStatus.ARCHIVED)
                .getSingleResult();
    }
}
