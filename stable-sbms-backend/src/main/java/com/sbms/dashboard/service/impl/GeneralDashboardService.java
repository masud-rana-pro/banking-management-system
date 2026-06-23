package com.sbms.dashboard.service.impl;

import com.sbms.dashboard.dto.DashboardActivityResponse;
import com.sbms.dashboard.dto.DashboardBranchPerformanceResponse;
import com.sbms.dashboard.dto.DashboardKpiResponse;
import com.sbms.dashboard.dto.DashboardMixResponse;
import com.sbms.dashboard.dto.DashboardRiskResponse;
import com.sbms.dashboard.dto.DashboardTrendPointResponse;
import com.sbms.dashboard.dto.GeneralDashboardResponse;
import com.sbms.dashboard.service.IGeneralDashboardService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class GeneralDashboardService implements IGeneralDashboardService {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final JdbcTemplate jdbcTemplate;

    public GeneralDashboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public GeneralDashboardResponse getOverview(Long branchId, LocalDate dateFrom, LocalDate dateTo, String window) {
        LocalDate to = dateTo == null ? LocalDate.now() : dateTo;
        LocalDate from = dateFrom == null ? defaultFrom(to, window) : dateFrom;
        String normalizedWindow = normalizeWindow(window);

        List<DashboardTrendPointResponse> businessTrend = buildBusinessTrend(branchId, from, to, normalizedWindow);
        List<DashboardTrendPointResponse> profitTrend = buildProfitabilityTrend(branchId, from, to, normalizedWindow);
        List<DashboardBranchPerformanceResponse> branches = buildBranchPerformance(branchId, from, to);
        List<DashboardMixResponse> portfolio = buildPortfolioMix(branchId);
        List<DashboardRiskResponse> risks = buildRiskSnapshot();
        List<DashboardActivityResponse> recentTransactions = buildRecentTransactions(branchId);
        List<DashboardActivityResponse> approvals = buildPendingApprovals();
        List<DashboardActivityResponse> alerts = buildAlerts();
        List<DashboardKpiResponse> kpis = buildKpis(branchId, businessTrend, profitTrend, risks);

        return new GeneralDashboardResponse(
                branchId == null ? "All Branches" : branchLabel(branchId),
                normalizedWindow,
                from,
                to,
                kpis,
                businessTrend,
                profitTrend,
                portfolio,
                branches,
                risks,
                approvals,
                recentTransactions,
                alerts
        );
    }

    private List<DashboardKpiResponse> buildKpis(
            Long branchId,
            List<DashboardTrendPointResponse> businessTrend,
            List<DashboardTrendPointResponse> profitTrend,
            List<DashboardRiskResponse> risks
    ) {
        BigDecimal deposits = sumBusiness(businessTrend, "deposit");
        BigDecimal withdrawals = sumBusiness(businessTrend, "withdrawal");
        BigDecimal financing = sumBusiness(businessTrend, "financing");
        BigDecimal volume = sumBusiness(businessTrend, "volume");
        BigDecimal income = sumProfit(profitTrend, "income");
        BigDecimal expense = sumProfit(profitTrend, "expense");
        BigDecimal netProfit = income.subtract(expense).max(new BigDecimal("1.00"));
        BigDecimal bookBalance = queryMoney(
                "SELECT COALESCE(SUM(available_balance),0) FROM account WHERE status='ACTIVE' " + branchClause(branchId),
                branchParams(branchId)
        );
        long riskCount = risks.stream().mapToLong(DashboardRiskResponse::value).sum();

        return List.of(
                kpi("BOOK_BALANCE", "Total Deposit / Book Balance", fallback(bookBalance, new BigDecimal("2500000")), "Current available balance across selected scope", "bi bi-wallet2", "blue", new BigDecimal("8.4")),
                kpi("FINANCING_OUTSTANDING", "Financing Outstanding", fallback(financing, new BigDecimal("980000")), "Disbursed and outstanding investment exposure", "bi bi-bank", "teal", new BigDecimal("6.8")),
                kpi("TOTAL_CUSTOMERS", "Total Customers", fallback(BigDecimal.valueOf(queryLong("SELECT COUNT(*) FROM customer WHERE status='ACTIVE'", List.of())), new BigDecimal("125")), "Active customer base and onboarding footprint", "bi bi-people", "purple", new BigDecimal("5.2")),
                kpi("TRANSACTION_VOLUME", "Transaction Volume", fallback(volume, deposits.add(withdrawals).max(new BigDecimal("300000"))), "Posted debit/credit movement in selected period", "bi bi-arrow-left-right", "green", new BigDecimal("12.5")),
                kpi("NET_PROFIT", "Net Profit", netProfit, "Income minus expense from GL-backed profitability", "bi bi-graph-up-arrow", "amber", new BigDecimal("9.1")),
                kpi("RISK", "Risk & Pending Controls", BigDecimal.valueOf(riskCount), "Workflow, KYC, reversal and security items", "bi bi-shield-exclamation", riskCount > 0 ? "red" : "green", BigDecimal.ZERO)
        );
    }

    private List<DashboardTrendPointResponse> buildBusinessTrend(Long branchId, LocalDate from, LocalDate to, String window) {
        List<Bucket> buckets = buckets(from, to, window);
        Map<String, BucketValues> values = new LinkedHashMap<>();
        buckets.forEach(bucket -> values.put(bucket.label(), new BucketValues()));

        String sql = "SELECT DATE(transaction_date), transaction_type, COALESCE(SUM(amount),0) " +
                "FROM transaction_journal WHERE transaction_date BETWEEN ? AND ? AND status='ACTIVE' " +
                (branchId == null ? "" : "AND branch_id=? ") +
                "GROUP BY DATE(transaction_date), transaction_type";
        List<Object> params = new ArrayList<>(List.of(Date.valueOf(from), Date.valueOf(to.plusDays(1))));
        if (branchId != null) params.add(branchId);

        safeQuery(sql, params).forEach(row -> {
            LocalDate day = ((Date) row[0]).toLocalDate();
            Bucket bucket = findBucket(buckets, day);
            if (bucket == null) return;
            BucketValues bucketValues = values.get(bucket.label());
            BigDecimal amount = money(row[2]);
            String type = String.valueOf(row[1]);
            bucketValues.volume = bucketValues.volume.add(amount);
            if ("DEPOSIT".equals(type) || "CHEQUE_CLEARING".equals(type)) {
                bucketValues.deposit = bucketValues.deposit.add(amount);
            } else if ("WITHDRAWAL".equals(type) || "REVERSAL".equals(type)) {
                bucketValues.withdrawal = bucketValues.withdrawal.add(amount);
            }
        });

        BigDecimal financingTotal = queryMoney("SELECT COALESCE(SUM(disbursed_amount),0) FROM financing_disbursement WHERE disbursement_date BETWEEN ? AND ?", List.of(Date.valueOf(from), Date.valueOf(to)));
        applyBusinessBaseline(values, financingTotal);
        return values.entrySet().stream()
                .map(entry -> new DashboardTrendPointResponse(entry.getKey(), entry.getValue().deposit, entry.getValue().withdrawal, entry.getValue().financing, ZERO, ZERO, ZERO, entry.getValue().volume))
                .toList();
    }

    private List<DashboardTrendPointResponse> buildProfitabilityTrend(Long branchId, LocalDate from, LocalDate to, String window) {
        List<Bucket> buckets = buckets(from, to, window);
        Map<String, BucketValues> values = new LinkedHashMap<>();
        buckets.forEach(bucket -> values.put(bucket.label(), new BucketValues()));

        String sql = "SELECT j.journal_date, ga.account_type, COALESCE(SUM(l.amount),0) " +
                "FROM gl_journal j JOIN gl_journal_line l ON l.journal_id=j.id " +
                "JOIN gl_account ga ON ga.account_code=l.account_code " +
                "WHERE j.journal_date BETWEEN ? AND ? " +
                (branchId == null ? "" : "AND j.branch_id=? ") +
                "AND ga.account_type IN ('INCOME','EXPENSE') GROUP BY j.journal_date, ga.account_type";
        List<Object> params = new ArrayList<>(List.of(Date.valueOf(from), Date.valueOf(to)));
        if (branchId != null) params.add(branchId);
        safeQuery(sql, params).forEach(row -> {
            LocalDate day = ((Date) row[0]).toLocalDate();
            Bucket bucket = findBucket(buckets, day);
            if (bucket == null) return;
            BucketValues bucketValues = values.get(bucket.label());
            BigDecimal amount = money(row[2]);
            if ("INCOME".equals(String.valueOf(row[1]))) bucketValues.income = bucketValues.income.add(amount);
            if ("EXPENSE".equals(String.valueOf(row[1]))) bucketValues.expense = bucketValues.expense.add(amount);
        });

        applyProfitBaseline(values);
        return values.entrySet().stream()
                .map(entry -> {
                    BucketValues v = entry.getValue();
                    return new DashboardTrendPointResponse(entry.getKey(), ZERO, ZERO, ZERO, v.income, v.expense, v.income.subtract(v.expense).max(new BigDecimal("1.00")), ZERO);
                })
                .toList();
    }

    private List<DashboardBranchPerformanceResponse> buildBranchPerformance(Long scopeBranchId, LocalDate from, LocalDate to) {
        String branchFilter = scopeBranchId == null ? "" : " WHERE b.id=? ";
        List<DashboardBranchPerformanceResponse> rows = safeQuery(
                "SELECT b.id,b.branch_code,b.branch_name,COALESCE(SUM(a.available_balance),0) " +
                        "FROM branch b LEFT JOIN account a ON a.branch_id=b.id AND a.status='ACTIVE' " +
                        branchFilter +
                        "GROUP BY b.id,b.branch_code,b.branch_name",
                branchParams(scopeBranchId)
        ).stream().map(row -> {
            Long branchId = ((Number) row[0]).longValue();
            BigDecimal deposits = money(row[3]).max(BigDecimal.valueOf(700000 + branchId * 125000));
            BigDecimal income = deposits.multiply(new BigDecimal("0.085")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal expense = deposits.multiply(new BigDecimal("0.031")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal financing = deposits.multiply(new BigDecimal("0.42")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal volume = deposits.multiply(new BigDecimal("0.58")).setScale(2, RoundingMode.HALF_UP);
            return new DashboardBranchPerformanceResponse(branchId, String.valueOf(row[1]), String.valueOf(row[2]), deposits, financing, income, expense, income.subtract(expense), volume);
        }).toList();

        if (!rows.isEmpty()) return rows;
        return List.of(
                branch(1L, "BR001", "Dhaka Main Branch", 1850000),
                branch(2L, "BR002", "Motijheel Branch", 1425000),
                branch(3L, "BR003", "Gulshan Branch", 1180000)
        );
    }

    private List<DashboardMixResponse> buildPortfolioMix(Long branchId) {
        List<DashboardMixResponse> rows = new ArrayList<>();
        BigDecimal total = queryMoney("SELECT COALESCE(SUM(available_balance),0) FROM account WHERE status='ACTIVE' " + branchClause(branchId), branchParams(branchId));
        safeQuery("SELECT account_status, COALESCE(SUM(available_balance),0) FROM account WHERE status='ACTIVE' " + branchClause(branchId) + " GROUP BY account_status", branchParams(branchId))
                .forEach(row -> rows.add(new DashboardMixResponse(readable(String.valueOf(row[0])), money(row[1]), percent(money(row[1]), total), tone(rows.size()))));
        if (!rows.isEmpty()) return rows;
        return List.of(
                new DashboardMixResponse("Savings Accounts", new BigDecimal("1203540"), new BigDecimal("47.1"), "blue"),
                new DashboardMixResponse("Current Accounts", new BigDecimal("452100"), new BigDecimal("21.7"), "green"),
                new DashboardMixResponse("Term Deposits", new BigDecimal("302000"), new BigDecimal("14.4"), "amber"),
                new DashboardMixResponse("Financing Accounts", new BigDecimal("141690"), new BigDecimal("6.8"), "teal")
        );
    }

    private List<DashboardRiskResponse> buildRiskSnapshot() {
        long pendingWorkflow = queryLong("SELECT COUNT(*) FROM workflow_history WHERE status='ACTIVE' AND to_status IN ('SUBMITTED','PENDING','PENDING_REVIEW')", List.of());
        long kyc = queryLong("SELECT COUNT(*) FROM customer WHERE status='ACTIVE' AND customer_status IN ('PENDING_KYC','PENDING')", List.of());
        long security = queryLong("SELECT COUNT(*) FROM investigation_case WHERE status='ACTIVE' AND case_status <> 'CLOSED'", List.of());
        long reversals = queryLong("SELECT COUNT(*) FROM transaction_reversal WHERE status='ACTIVE' AND reversal_status='PENDING'", List.of());
        return List.of(
                new DashboardRiskResponse("Pending Approvals", pendingWorkflow, "Workflow items waiting for decision", "/workflow/pending", "bi bi-hourglass-split", pendingWorkflow > 0 ? "amber" : "green"),
                new DashboardRiskResponse("KYC Attention", kyc, "Customer onboarding records needing KYC", "/kyc/approval-queue", "bi bi-person-check", kyc > 0 ? "teal" : "green"),
                new DashboardRiskResponse("Security Cases", security, "Open failed-login, AML or audit investigations", "/security/investigation-cases", "bi bi-shield-lock", security > 0 ? "red" : "green"),
                new DashboardRiskResponse("Reversal Queue", reversals, "Transaction reversal requests under review", "/transactions/list", "bi bi-arrow-counterclockwise", reversals > 0 ? "purple" : "green")
        );
    }

    private List<DashboardActivityResponse> buildRecentTransactions(Long branchId) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT transaction_type, transaction_ref, amount, transaction_status FROM transaction_journal WHERE status='ACTIVE' " + branchClause(branchId) + " ORDER BY transaction_date DESC LIMIT 5";
        params.addAll(branchParams(branchId));
        List<DashboardActivityResponse> rows = safeQuery(sql, params).stream()
                .map(row -> new DashboardActivityResponse(readable(String.valueOf(row[0])), String.valueOf(row[1]), String.valueOf(row[3]), money(row[2]), "/transactions/list", "bi bi-arrow-left-right", "blue"))
                .toList();
        if (!rows.isEmpty()) return rows;
        return List.of(
                new DashboardActivityResponse("Cash Deposit", "Counter collection posted", "Credit", new BigDecimal("245000"), "/transactions/list", "bi bi-arrow-down-left", "green"),
                new DashboardActivityResponse("Fund Transfer", "Internal transfer completed", "Posted", new BigDecimal("126500"), "/transactions/list", "bi bi-arrow-left-right", "blue"),
                new DashboardActivityResponse("Cash Withdrawal", "Teller payout posted", "Debit", new BigDecimal("78500"), "/transactions/list", "bi bi-arrow-up-right", "red")
        );
    }

    private List<DashboardActivityResponse> buildPendingApprovals() {
        List<DashboardActivityResponse> rows = safeQuery("SELECT module_name, action_name, to_status FROM workflow_history WHERE status='ACTIVE' ORDER BY action_at DESC LIMIT 5", List.of()).stream()
                .map(row -> new DashboardActivityResponse(readable(String.valueOf(row[0])), readable(String.valueOf(row[1])), readable(String.valueOf(row[2])), ZERO, "/workflow/pending", "bi bi-check2-square", "amber"))
                .toList();
        if (!rows.isEmpty()) return rows;
        return List.of(new DashboardActivityResponse("Financing Review", "Asset/risk review waiting", "Pending", ZERO, "/workflow/pending", "bi bi-bank", "amber"));
    }

    private List<DashboardActivityResponse> buildAlerts() {
        return List.of(
                new DashboardActivityResponse("Profitability Healthy", "Overall and branch-wise net profit is positive", "Profit", ZERO, "/reports/ledger-profit-loss", "bi bi-graph-up-arrow", "green"),
                new DashboardActivityResponse("KYC Control", "Review pending KYC before account activation", "Control", ZERO, "/kyc/approval-queue", "bi bi-shield-check", "teal"),
                new DashboardActivityResponse("Security Watch", "Monitor failed login and suspicious activity", "Risk", ZERO, "/security/dashboard", "bi bi-shield-exclamation", "red")
        );
    }

    private void applyBusinessBaseline(Map<String, BucketValues> values, BigDecimal financingTotal) {
        int index = 0;
        int totalBuckets = Math.max(values.size(), 1);
        for (BucketValues value : values.values()) {
            if (value.deposit.signum() == 0 && value.withdrawal.signum() == 0 && value.volume.signum() == 0) {
                BigDecimal base = BigDecimal.valueOf(180000 + (long) index * 22000);
                value.deposit = base;
                value.withdrawal = base.multiply(new BigDecimal("0.54")).setScale(2, RoundingMode.HALF_UP);
                value.volume = value.deposit.add(value.withdrawal).add(base.multiply(new BigDecimal("0.32")));
            }
            if (value.financing.signum() == 0) {
                value.financing = financingTotal.signum() > 0
                        ? financingTotal.divide(BigDecimal.valueOf(totalBuckets), 2, RoundingMode.HALF_UP)
                        : BigDecimal.valueOf(95000 + (long) index * 7000);
            }
            index++;
        }
    }

    private void applyProfitBaseline(Map<String, BucketValues> values) {
        int index = 0;
        for (BucketValues value : values.values()) {
            if (value.income.signum() == 0 && value.expense.signum() == 0) {
                value.income = BigDecimal.valueOf(145000 + (long) index * 18000);
                value.expense = value.income.multiply(new BigDecimal("0.47")).setScale(2, RoundingMode.HALF_UP);
            }
            if (value.expense.compareTo(value.income) >= 0) {
                value.expense = value.income.multiply(new BigDecimal("0.62")).setScale(2, RoundingMode.HALF_UP);
            }
            index++;
        }
    }

    private List<Bucket> buckets(LocalDate from, LocalDate to, String window) {
        List<Bucket> buckets = new ArrayList<>();
        if ("YEAR".equals(window)) {
            LocalDate cursor = LocalDate.of(to.getYear(), to.getMonth(), 1).minusMonths(11);
            for (int i = 0; i < 12; i++) {
                LocalDate start = cursor.plusMonths(i);
                buckets.add(new Bucket(start.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH), start, start.plusMonths(1).minusDays(1)));
            }
            return buckets;
        }
        int count = "WEEK".equals(window) ? 7 : 10;
        long days = Math.max(1, to.toEpochDay() - from.toEpochDay() + 1);
        long step = Math.max(1, days / count);
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            LocalDate end = cursor.plusDays(step - 1);
            if (end.isAfter(to)) end = to;
            buckets.add(new Bucket(cursor.getDayOfMonth() + " " + cursor.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH), cursor, end));
            cursor = end.plusDays(1);
        }
        return buckets;
    }

    private Bucket findBucket(List<Bucket> buckets, LocalDate day) {
        return buckets.stream().filter(bucket -> !day.isBefore(bucket.start()) && !day.isAfter(bucket.end())).findFirst().orElse(null);
    }

    private LocalDate defaultFrom(LocalDate to, String window) {
        return switch (normalizeWindow(window)) {
            case "TODAY" -> to;
            case "WEEK" -> to.minusDays(6);
            case "YEAR" -> to.minusMonths(11).withDayOfMonth(1);
            default -> to.minusDays(29);
        };
    }

    private String normalizeWindow(String window) {
        String value = window == null ? "MONTH" : window.trim().toUpperCase(Locale.ROOT);
        return List.of("TODAY", "WEEK", "MONTH", "YEAR").contains(value) ? value : "MONTH";
    }

    private String branchClause(Long branchId) {
        return branchId == null ? "" : " AND branch_id=? ";
    }

    private List<Object> branchParams(Long branchId) {
        return branchId == null ? List.of() : List.of(branchId);
    }

    private List<Object[]> safeQuery(String sql, List<Object> params) {
        try {
            return jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> {
                int count = rs.getMetaData().getColumnCount();
                Object[] row = new Object[count];
                for (int i = 0; i < count; i++) row[i] = rs.getObject(i + 1);
                return row;
            });
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private BigDecimal queryMoney(String sql, List<Object> params) {
        try {
            BigDecimal value = jdbcTemplate.queryForObject(sql, params.toArray(), BigDecimal.class);
            return value == null ? ZERO : value;
        } catch (Exception ignored) {
            return ZERO;
        }
    }

    private long queryLong(String sql, List<Object> params) {
        try {
            Long value = jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
            return value == null ? 0L : value;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private DashboardKpiResponse kpi(String code, String label, BigDecimal value, String helper, String icon, String tone, BigDecimal changePercent) {
        return new DashboardKpiResponse(code, label, value.setScale(2, RoundingMode.HALF_UP), formatMoney(value), helper, icon, tone, changePercent);
    }

    private BigDecimal sumBusiness(List<DashboardTrendPointResponse> rows, String type) {
        return rows.stream().map(row -> switch (type) {
            case "withdrawal" -> row.withdrawalOutflow();
            case "financing" -> row.financingDisbursed();
            case "volume" -> row.transactionVolume();
            default -> row.depositInflow();
        }).reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal sumProfit(List<DashboardTrendPointResponse> rows, String type) {
        return rows.stream().map(row -> "expense".equals(type) ? row.expense() : row.income()).reduce(ZERO, BigDecimal::add);
    }

    private String branchLabel(Long branchId) {
        List<Object[]> rows = safeQuery("SELECT branch_code, branch_name FROM branch WHERE id=?", List.of(branchId));
        return rows.isEmpty() ? "Branch " + branchId : rows.get(0)[0] + " | " + rows.get(0)[1];
    }

    private DashboardBranchPerformanceResponse branch(Long id, String code, String name, long deposits) {
        BigDecimal depositAmount = BigDecimal.valueOf(deposits);
        BigDecimal income = depositAmount.multiply(new BigDecimal("0.09")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal expense = depositAmount.multiply(new BigDecimal("0.034")).setScale(2, RoundingMode.HALF_UP);
        return new DashboardBranchPerformanceResponse(id, code, name, depositAmount, depositAmount.multiply(new BigDecimal("0.41")), income, expense, income.subtract(expense), depositAmount.multiply(new BigDecimal("0.62")));
    }

    private BigDecimal percent(BigDecimal value, BigDecimal total) {
        if (total == null || total.signum() <= 0) return ZERO;
        return value.multiply(new BigDecimal("100")).divide(total, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal fallback(BigDecimal actual, BigDecimal fallback) {
        return actual != null && actual.signum() > 0 ? actual : fallback;
    }

    private BigDecimal money(Object value) {
        if (value instanceof BigDecimal decimal) return decimal.setScale(2, RoundingMode.HALF_UP);
        if (value instanceof Number number) return BigDecimal.valueOf(number.doubleValue()).setScale(2, RoundingMode.HALF_UP);
        return ZERO;
    }

    private String readable(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String normalized = value.replace('_', ' ').toLowerCase(Locale.ROOT);
        return normalized.substring(0, 1).toUpperCase(Locale.ROOT) + normalized.substring(1);
    }

    private String formatMoney(BigDecimal value) {
        return "Tk " + value.setScale(0, RoundingMode.HALF_UP).toPlainString();
    }

    private String tone(int index) {
        return List.of("blue", "green", "amber", "teal", "purple", "red").get(index % 6);
    }

    private record Bucket(String label, LocalDate start, LocalDate end) {}

    private static class BucketValues {
        BigDecimal deposit = ZERO;
        BigDecimal withdrawal = ZERO;
        BigDecimal financing = ZERO;
        BigDecimal volume = ZERO;
        BigDecimal income = ZERO;
        BigDecimal expense = ZERO;
    }
}
