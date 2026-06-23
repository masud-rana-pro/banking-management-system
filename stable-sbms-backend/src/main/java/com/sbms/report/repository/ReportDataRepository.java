package com.sbms.report.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class ReportDataRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> findOperationalRows(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        Query query = entityManager.createNativeQuery("""
                SELECT
                    COALESCE(t.transaction_type, 'UNKNOWN') AS category,
                    COUNT(*) AS item_count,
                    COALESCE(SUM(t.amount), 0) AS total_amount,
                    COALESCE(SUM(CASE WHEN t.transaction_status = 'POSTED' THEN 1 ELSE 0 END), 0) AS posted_count,
                    COALESCE(SUM(CASE WHEN t.reversal_flag = 1 THEN 1 ELSE 0 END), 0) AS reversed_count
                FROM transaction_journal t
                WHERE DATE(t.transaction_date) BETWEEN :dateFrom AND :dateTo
                  AND (:branchId IS NULL OR t.branch_id = :branchId)
                GROUP BY t.transaction_type
                ORDER BY total_amount DESC, item_count DESC
                """);
        applyDateBranch(query, dateFrom, dateTo, branchId);
        return query.getResultList();
    }

    public List<Object[]> findProfitDistributionRows(LocalDate dateFrom, LocalDate dateTo) {
        Query query = entityManager.createNativeQuery("""
                SELECT
                    COALESCE(p.status, 'PENDING') AS category,
                    COUNT(*) AS item_count,
                    COALESCE(SUM(p.profit_amount), 0) AS total_amount,
                    MAX(p.posting_date) AS latest_date
                FROM profit_posting p
                WHERE p.posting_date BETWEEN :dateFrom AND :dateTo
                GROUP BY p.status
                ORDER BY total_amount DESC, item_count DESC
                """);
        query.setParameter("dateFrom", Date.valueOf(dateFrom));
        query.setParameter("dateTo", Date.valueOf(dateTo));
        return query.getResultList();
    }

    public List<Object[]> findFinancingPortfolioRows(LocalDate dateFrom, LocalDate dateTo) {
        Query query = entityManager.createNativeQuery("""
                SELECT
                    COALESCE(a.application_status, 'DRAFT') AS category,
                    COUNT(*) AS item_count,
                    COALESCE(SUM(a.requested_amount), 0) AS total_amount,
                    COALESCE(SUM(CASE WHEN a.approved_at IS NOT NULL THEN 1 ELSE 0 END), 0) AS approved_count
                FROM financing_application a
                WHERE DATE(a.created_at) BETWEEN :dateFrom AND :dateTo
                GROUP BY a.application_status
                ORDER BY total_amount DESC, item_count DESC
                """);
        query.setParameter("dateFrom", Date.valueOf(dateFrom));
        query.setParameter("dateTo", Date.valueOf(dateTo));
        return query.getResultList();
    }

    public List<Object[]> findParRows(LocalDate dateFrom, LocalDate dateTo) {
        Query query = entityManager.createNativeQuery("""
                SELECT
                    COALESCE(fp.product_name, 'UNASSIGNED') AS category,
                    COUNT(fs.id) AS item_count,
                    COALESCE(SUM(
                        (COALESCE(fs.principal_amount, 0) + COALESCE(fs.profit_amount, 0) + COALESCE(fs.charity_amount, 0))
                        - COALESCE(fs.paid_amount, 0)
                    ), 0) AS outstanding_amount,
                    COALESCE(SUM(CASE
                        WHEN fs.due_date < CURRENT_DATE
                         AND ((COALESCE(fs.principal_amount, 0) + COALESCE(fs.profit_amount, 0) + COALESCE(fs.charity_amount, 0))
                         - COALESCE(fs.paid_amount, 0)) > 0
                        THEN 1 ELSE 0 END), 0) AS overdue_count
                FROM financing_schedule fs
                JOIN financing_application fa ON fa.id = fs.application_id
                JOIN financing_product fp ON fp.id = fa.product_id
                WHERE fs.due_date BETWEEN :dateFrom AND :dateTo
                GROUP BY fp.product_name
                ORDER BY outstanding_amount DESC, item_count DESC
                """);
        query.setParameter("dateFrom", Date.valueOf(dateFrom));
        query.setParameter("dateTo", Date.valueOf(dateTo));
        return query.getResultList();
    }

    public List<Object[]> findShariahAuditRows(LocalDate dateFrom, LocalDate dateTo) {
        Query query = entityManager.createNativeQuery("""
                SELECT
                    COALESCE(s.reference_module, 'GENERAL') AS module_name,
                    COALESCE(s.case_status, 'PENDING_REVIEW') AS case_status,
                    COUNT(*) AS item_count,
                    MAX(s.submitted_at) AS latest_date
                FROM shariah_review_case s
                WHERE DATE(s.submitted_at) BETWEEN :dateFrom AND :dateTo
                GROUP BY s.reference_module, s.case_status
                ORDER BY item_count DESC, module_name ASC
                """);
        query.setParameter("dateFrom", Date.valueOf(dateFrom));
        query.setParameter("dateTo", Date.valueOf(dateTo));
        return query.getResultList();
    }

    public List<Object[]> findBranchRows(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        Query query = entityManager.createNativeQuery("""
                SELECT
                    COALESCE(b.branch_code, CONCAT('BR-', t.branch_id)) AS branch_code,
                    COALESCE(b.branch_name, 'Unknown Branch') AS branch_name,
                    COUNT(t.id) AS item_count,
                    COALESCE(SUM(t.amount), 0) AS total_amount,
                    COALESCE(SUM(CASE WHEN t.channel_type IN ('ATM', 'CDM') THEN 1 ELSE 0 END), 0) AS self_service_count
                FROM transaction_journal t
                LEFT JOIN branch b ON b.id = t.branch_id
                WHERE DATE(t.transaction_date) BETWEEN :dateFrom AND :dateTo
                  AND (:branchId IS NULL OR t.branch_id = :branchId)
                GROUP BY t.branch_id, b.branch_code, b.branch_name
                ORDER BY total_amount DESC, item_count DESC
                """);
        applyDateBranch(query, dateFrom, dateTo, branchId);
        return query.getResultList();
    }

    public List<Object[]> findKpiRows(LocalDate dateFrom, LocalDate dateTo) {
        Query query = entityManager.createNativeQuery("""
                SELECT 'CUSTOMERS' AS metric_group,
                       COALESCE(SUM(CASE WHEN DATE(c.created_at) BETWEEN :dateFrom AND :dateTo THEN 1 ELSE 0 END), 0) AS period_count,
                       COALESCE(SUM(CASE WHEN c.customer_status = 'ACTIVE' THEN 1 ELSE 0 END), 0) AS active_count,
                       COALESCE(SUM(CASE WHEN c.customer_status IN ('PENDING_KYC', 'BLOCKED', 'REJECTED') THEN 1 ELSE 0 END), 0) AS flagged_count
                FROM customer c
                UNION ALL
                SELECT 'ACCOUNTS' AS metric_group,
                       COALESCE(SUM(CASE WHEN DATE(a.created_at) BETWEEN :dateFrom AND :dateTo THEN 1 ELSE 0 END), 0) AS period_count,
                       COALESCE(SUM(CASE WHEN a.account_status = 'ACTIVE' THEN 1 ELSE 0 END), 0) AS active_count,
                       COALESCE(SUM(CASE WHEN a.account_status IN ('PENDING', 'SUSPENDED', 'CLOSED') THEN 1 ELSE 0 END), 0) AS flagged_count
                FROM account a
                UNION ALL
                SELECT 'FINANCING' AS metric_group,
                       COALESCE(SUM(CASE WHEN DATE(f.created_at) BETWEEN :dateFrom AND :dateTo THEN 1 ELSE 0 END), 0) AS period_count,
                       COALESCE(SUM(CASE WHEN f.application_status IN ('APPROVED', 'DISBURSED', 'ACTIVE') THEN 1 ELSE 0 END), 0) AS active_count,
                       COALESCE(SUM(CASE WHEN f.application_status IN ('RETURNED', 'REJECTED', 'SHARIAH_REVIEW') THEN 1 ELSE 0 END), 0) AS flagged_count
                FROM financing_application f
                UNION ALL
                SELECT 'TRANSACTIONS' AS metric_group,
                       COALESCE(SUM(CASE WHEN DATE(t.transaction_date) BETWEEN :dateFrom AND :dateTo THEN 1 ELSE 0 END), 0) AS period_count,
                       COALESCE(SUM(CASE WHEN t.transaction_status = 'POSTED' THEN 1 ELSE 0 END), 0) AS active_count,
                       COALESCE(SUM(CASE WHEN t.reversal_flag = 1 OR t.transaction_status = 'REVERSED' THEN 1 ELSE 0 END), 0) AS flagged_count
                FROM transaction_journal t
                """);
        query.setParameter("dateFrom", Date.valueOf(dateFrom));
        query.setParameter("dateTo", Date.valueOf(dateTo));
        return query.getResultList();
    }

    public List<Object[]> findGrowthRows(LocalDate dateFrom, LocalDate dateTo) {
        Query query = entityManager.createNativeQuery("""
                SELECT period_label,
                       SUM(customer_count) AS customer_count,
                       SUM(account_count) AS account_count,
                       SUM(financing_count) AS financing_count
                FROM (
                    SELECT DATE_FORMAT(c.created_at, '%Y-%m') AS period_label,
                           COUNT(*) AS customer_count,
                           0 AS account_count,
                           0 AS financing_count
                    FROM customer c
                    WHERE DATE(c.created_at) BETWEEN :dateFrom AND :dateTo
                    GROUP BY DATE_FORMAT(c.created_at, '%Y-%m')
                    UNION ALL
                    SELECT DATE_FORMAT(a.opened_at, '%Y-%m') AS period_label,
                           0 AS customer_count,
                           COUNT(*) AS account_count,
                           0 AS financing_count
                    FROM account a
                    WHERE DATE(a.opened_at) BETWEEN :dateFrom AND :dateTo
                    GROUP BY DATE_FORMAT(a.opened_at, '%Y-%m')
                    UNION ALL
                    SELECT DATE_FORMAT(f.created_at, '%Y-%m') AS period_label,
                           0 AS customer_count,
                           0 AS account_count,
                           COUNT(*) AS financing_count
                    FROM financing_application f
                    WHERE DATE(f.created_at) BETWEEN :dateFrom AND :dateTo
                    GROUP BY DATE_FORMAT(f.created_at, '%Y-%m')
                ) growth_rows
                GROUP BY period_label
                ORDER BY period_label ASC
                """);
        query.setParameter("dateFrom", Date.valueOf(dateFrom));
        query.setParameter("dateTo", Date.valueOf(dateTo));
        return query.getResultList();
    }

    public List<Object[]> findLoanRecoveryRows(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        Query query = entityManager.createNativeQuery("""
                SELECT
                    COALESCE(fp.product_name, 'UNASSIGNED') AS product_name,
                    COUNT(DISTINCT fa.id) AS application_count,
                    COALESCE(SUM(fs.paid_amount), 0) AS recovered_amount,
                    COALESCE(SUM(CASE
                        WHEN fs.due_date < CURRENT_DATE
                         AND ((COALESCE(fs.principal_amount, 0) + COALESCE(fs.profit_amount, 0) + COALESCE(fs.charity_amount, 0))
                         - COALESCE(fs.paid_amount, 0)) > 0
                        THEN ((COALESCE(fs.principal_amount, 0) + COALESCE(fs.profit_amount, 0) + COALESCE(fs.charity_amount, 0))
                              - COALESCE(fs.paid_amount, 0))
                        ELSE 0 END), 0) AS overdue_amount
                FROM financing_schedule fs
                JOIN financing_application fa ON fa.id = fs.application_id
                JOIN financing_product fp ON fp.id = fa.product_id
                WHERE fs.due_date BETWEEN :dateFrom AND :dateTo
                  AND (:branchId IS NULL OR fa.branch_id = :branchId)
                GROUP BY fp.product_name
                ORDER BY overdue_amount DESC, recovered_amount DESC
                """);
        applyDateBranch(query, dateFrom, dateTo, branchId);
        return query.getResultList();
    }

    public List<Object[]> findMonthlyClosingRows(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        Query query = entityManager.createNativeQuery("""
                SELECT
                    COALESCE(b.branch_code, CONCAT('BR-', txn.branch_id)) AS branch_code,
                    COALESCE(b.branch_name, 'Unknown Branch') AS branch_name,
                    COALESCE(txn.total_amount, 0) AS transaction_amount,
                    COALESCE(txn.reversed_count, 0) AS reversed_count,
                    COALESCE(vault.closing_balance, 0) AS latest_vault_closing_balance,
                    COALESCE(profit.total_profit, 0) AS profit_posted
                FROM (
                    SELECT
                        t.branch_id,
                        SUM(t.amount) AS total_amount,
                        SUM(CASE WHEN t.reversal_flag = 1 OR t.transaction_status = 'REVERSED' THEN 1 ELSE 0 END) AS reversed_count
                    FROM transaction_journal t
                    WHERE DATE(t.transaction_date) BETWEEN :dateFrom AND :dateTo
                    GROUP BY t.branch_id
                ) txn
                LEFT JOIN branch b ON b.id = txn.branch_id
                LEFT JOIN (
                    SELECT v1.branch_id, v1.closing_balance
                    FROM vault_balance v1
                    JOIN (
                        SELECT branch_id, MAX(balance_date) AS max_date
                        FROM vault_balance
                        WHERE balance_date BETWEEN :dateFrom AND :dateTo
                        GROUP BY branch_id
                    ) v2 ON v2.branch_id = v1.branch_id AND v2.max_date = v1.balance_date
                ) vault ON vault.branch_id = txn.branch_id
                LEFT JOIN (
                    SELECT a.branch_id, SUM(p.profit_amount) AS total_profit
                    FROM profit_posting p
                    JOIN account a ON a.id = p.account_id
                    WHERE p.posting_date BETWEEN :dateFrom AND :dateTo
                    GROUP BY a.branch_id
                ) profit ON profit.branch_id = txn.branch_id
                WHERE (:branchId IS NULL OR txn.branch_id = :branchId)
                ORDER BY transaction_amount DESC, branch_name ASC
                """);
        applyDateBranch(query, dateFrom, dateTo, branchId);
        return query.getResultList();
    }

    public java.util.Optional<Object[]> findMonthlyClosingSnapshot(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        Query query = entityManager.createNativeQuery("""
                SELECT
                    b.branch_code,
                    b.branch_name,
                    COALESCE(txn.total_amount, 0) AS transaction_amount,
                    COALESCE(txn.reversed_count, 0) AS reversed_count,
                    COALESCE(vault.closing_balance, 0) AS latest_vault_closing_balance,
                    COALESCE(profit.total_profit, 0) AS profit_posted
                FROM branch b
                LEFT JOIN (
                    SELECT
                        t.branch_id,
                        SUM(t.amount) AS total_amount,
                        SUM(CASE WHEN t.reversal_flag = 1 OR t.transaction_status = 'REVERSED' THEN 1 ELSE 0 END) AS reversed_count
                    FROM transaction_journal t
                    WHERE DATE(t.transaction_date) BETWEEN :dateFrom AND :dateTo
                    GROUP BY t.branch_id
                ) txn ON txn.branch_id = b.id
                LEFT JOIN (
                    SELECT v1.branch_id, v1.closing_balance
                    FROM vault_balance v1
                    JOIN (
                        SELECT branch_id, MAX(balance_date) AS max_date
                        FROM vault_balance
                        WHERE balance_date BETWEEN :dateFrom AND :dateTo
                        GROUP BY branch_id
                    ) v2 ON v2.branch_id = v1.branch_id AND v2.max_date = v1.balance_date
                ) vault ON vault.branch_id = b.id
                LEFT JOIN (
                    SELECT a.branch_id, SUM(p.profit_amount) AS total_profit
                    FROM profit_posting p
                    JOIN account a ON a.id = p.account_id
                    WHERE p.posting_date BETWEEN :dateFrom AND :dateTo
                    GROUP BY a.branch_id
                ) profit ON profit.branch_id = b.id
                WHERE b.id = :branchId
                """);
        query.setParameter("dateFrom", Date.valueOf(dateFrom));
        query.setParameter("dateTo", Date.valueOf(dateTo));
        query.setParameter("branchId", branchId);
        List<?> result = query.getResultList();
        if (result.isEmpty()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of((Object[]) result.get(0));
    }

    public BigDecimal sumBranchVolume(LocalDate dateFrom, LocalDate dateTo) {
        return toBigDecimal(entityManager.createNativeQuery("""
                        SELECT COALESCE(SUM(t.amount), 0)
                        FROM transaction_journal t
                        WHERE DATE(t.transaction_date) BETWEEN :dateFrom AND :dateTo
                        """)
                .setParameter("dateFrom", Date.valueOf(dateFrom))
                .setParameter("dateTo", Date.valueOf(dateTo))
                .getSingleResult());
    }

    public BigDecimal sumFinancingAmount(LocalDate dateFrom, LocalDate dateTo) {
        return toBigDecimal(entityManager.createNativeQuery("""
                        SELECT COALESCE(SUM(a.requested_amount), 0)
                        FROM financing_application a
                        WHERE DATE(a.created_at) BETWEEN :dateFrom AND :dateTo
                        """)
                .setParameter("dateFrom", Date.valueOf(dateFrom))
                .setParameter("dateTo", Date.valueOf(dateTo))
                .getSingleResult());
    }

    public BigDecimal sumProfitAmount(LocalDate dateFrom, LocalDate dateTo) {
        return toBigDecimal(entityManager.createNativeQuery("""
                        SELECT COALESCE(SUM(p.profit_amount), 0)
                        FROM profit_posting p
                        WHERE p.posting_date BETWEEN :dateFrom AND :dateTo
                        """)
                .setParameter("dateFrom", Date.valueOf(dateFrom))
                .setParameter("dateTo", Date.valueOf(dateTo))
                .getSingleResult());
    }

    public BigDecimal sumRealizedFinancingProfitProxy(LocalDate dateFrom, LocalDate dateTo) {
        return toBigDecimal(entityManager.createNativeQuery("""
                        SELECT COALESCE(SUM(fs.profit_amount), 0)
                        FROM financing_schedule fs
                        WHERE fs.schedule_status = 'PAID'
                          AND fs.paid_date BETWEEN :dateFrom AND :dateTo
                        """)
                .setParameter("dateFrom", Date.valueOf(dateFrom))
                .setParameter("dateTo", Date.valueOf(dateTo))
                .getSingleResult());
    }

    public long countRealizedFinancingSchedules(LocalDate dateFrom, LocalDate dateTo) {
        return toLong(entityManager.createNativeQuery("""
                        SELECT COALESCE(COUNT(fs.id), 0)
                        FROM financing_schedule fs
                        WHERE fs.schedule_status = 'PAID'
                          AND fs.paid_date BETWEEN :dateFrom AND :dateTo
                        """)
                .setParameter("dateFrom", Date.valueOf(dateFrom))
                .setParameter("dateTo", Date.valueOf(dateTo))
                .getSingleResult());
    }

    public long countProfitPostingRecords(LocalDate dateFrom, LocalDate dateTo) {
        return toLong(entityManager.createNativeQuery("""
                        SELECT COALESCE(COUNT(p.id), 0)
                        FROM profit_posting p
                        WHERE p.posting_date BETWEEN :dateFrom AND :dateTo
                        """)
                .setParameter("dateFrom", Date.valueOf(dateFrom))
                .setParameter("dateTo", Date.valueOf(dateTo))
                .getSingleResult());
    }

    public long countFinancingApplications(LocalDate dateFrom, LocalDate dateTo) {
        return toLong(entityManager.createNativeQuery("""
                        SELECT COALESCE(COUNT(a.id), 0)
                        FROM financing_application a
                        WHERE DATE(a.created_at) BETWEEN :dateFrom AND :dateTo
                        """)
                .setParameter("dateFrom", Date.valueOf(dateFrom))
                .setParameter("dateTo", Date.valueOf(dateTo))
                .getSingleResult());
    }

    public long countTransactions(LocalDate dateFrom, LocalDate dateTo) {
        return toLong(entityManager.createNativeQuery("""
                        SELECT COALESCE(COUNT(t.id), 0)
                        FROM transaction_journal t
                        WHERE DATE(t.transaction_date) BETWEEN :dateFrom AND :dateTo
                        """)
                .setParameter("dateFrom", Date.valueOf(dateFrom))
                .setParameter("dateTo", Date.valueOf(dateTo))
                .getSingleResult());
    }

    private void applyDateBranch(Query query, LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        query.setParameter("dateFrom", Date.valueOf(dateFrom));
        query.setParameter("dateTo", Date.valueOf(dateTo));
        query.setParameter("branchId", branchId);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        return new BigDecimal(String.valueOf(value));
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }
}
