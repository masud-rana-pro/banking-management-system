package com.sbms.calculation.service;

import com.sbms.calculation.dto.request.CalculationSimulateRequest;
import com.sbms.calculation.dto.response.*;
import com.sbms.common.exception.BadRequestException;
import com.sbms.depositscheme.entity.DepositScheme;
import com.sbms.depositscheme.entity.DepositSchemeEnrollment;
import com.sbms.depositscheme.repository.DepositSchemeEnrollmentRepository;
import com.sbms.depositscheme.repository.DepositSchemeRepository;
import com.sbms.financing.entity.FinancingApplication;
import com.sbms.financing.entity.FinancingProduct;
import com.sbms.financing.repository.FinancingApplicationRepository;
import com.sbms.financing.repository.FinancingProductRepository;
import com.sbms.profit.entity.ProfitPosting;
import com.sbms.profit.enums.ProfitFrequency;
import com.sbms.profit.enums.ProfitPostingStatus;
import com.sbms.profit.repository.ProfitPostingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class CalculationEngineService {

    private final FinancingProductRepository financingProductRepository;
    private final FinancingApplicationRepository financingApplicationRepository;
    private final DepositSchemeRepository depositSchemeRepository;
    private final DepositSchemeEnrollmentRepository depositSchemeEnrollmentRepository;
    private final ProfitPostingRepository profitPostingRepository;

    public CalculationEngineService(
            FinancingProductRepository financingProductRepository,
            FinancingApplicationRepository financingApplicationRepository,
            DepositSchemeRepository depositSchemeRepository,
            DepositSchemeEnrollmentRepository depositSchemeEnrollmentRepository,
            ProfitPostingRepository profitPostingRepository
    ) {
        this.financingProductRepository = financingProductRepository;
        this.financingApplicationRepository = financingApplicationRepository;
        this.depositSchemeRepository = depositSchemeRepository;
        this.depositSchemeEnrollmentRepository = depositSchemeEnrollmentRepository;
        this.profitPostingRepository = profitPostingRepository;
    }

    public CalculationDashboardSummaryResponse dashboardSummary() {
        List<FinancingApplication> recentFinancing = financingApplicationRepository.findLatest(5);
        List<DepositSchemeEnrollment> recentDeposits = depositSchemeEnrollmentRepository.findLatest(5);
        List<ProfitPosting> recentFailedProfit = profitPostingRepository.findRecentFailed(5);

        List<CalculationRecentItemResponse> recentItems = new ArrayList<>();
        recentFinancing.forEach(item -> recentItems.add(new CalculationRecentItemResponse(
                "FINANCING",
                item.getApplicationNo(),
                item.getProduct().getFinancingType().name(),
                item.getCustomer().getCustomerCode() + " - " + item.getCustomer().getFullName(),
                scaleMoney(item.getRequestedAmount()),
                item.getApplicationStatus().name(),
                item.getCreatedAt().toLocalDate(),
                "/financing/applications/" + item.getId()
        )));
        recentDeposits.forEach(item -> recentItems.add(new CalculationRecentItemResponse(
                "DEPOSIT_SCHEME",
                item.getEnrollmentNo(),
                item.getScheme().getSchemeType().name(),
                item.getCustomer().getCustomerCode() + " - " + item.getCustomer().getFullName(),
                scaleMoney(item.getInstallmentAmount()),
                item.getEnrollmentStatus().name(),
                item.getCreatedAt().toLocalDate(),
                "/deposit-schemes/enrollments/" + item.getId()
        )));
        recentFailedProfit.forEach(item -> recentItems.add(new CalculationRecentItemResponse(
                "PROFIT_POSTING",
                item.getPostingRef(),
                item.getSchedule().getProfitFrequency().name(),
                item.getAccount().getAccountNumber(),
                scaleMoney(item.getProfitAmount()),
                item.getStatus().name(),
                item.getPostingDate(),
                "/profit/postings/" + item.getId()
        )));
        recentItems.sort(Comparator.comparing(CalculationRecentItemResponse::eventDate).reversed());
        List<CalculationRecentItemResponse> dashboardRecentItems = recentItems.size() > 10
                ? new ArrayList<>(recentItems.subList(0, 10))
                : recentItems;

        Map<String, Long> counts = new LinkedHashMap<>();
        for (FinancingProduct product : financingProductRepository.findAll()) {
            counts.merge(product.getFinancingType().name(), 1L, Long::sum);
        }
        for (DepositScheme scheme : depositSchemeRepository.findAll()) {
            counts.merge(scheme.getSchemeType().name(), 1L, Long::sum);
        }
        counts.merge("PROFIT_POSTING", profitPostingRepository.countByStatus(ProfitPostingStatus.POSTED), Long::sum);

        List<CalculationMetricResponse> metrics = counts.entrySet().stream()
                .map(entry -> new CalculationMetricResponse(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CalculationMetricResponse::usageCount).reversed())
                .toList();

        return new CalculationDashboardSummaryResponse(
                dashboardRecentItems.size(),
                profitPostingRepository.countByStatus(ProfitPostingStatus.FAILED),
                financingProductRepository.countActiveOrPending(),
                depositSchemeRepository.countTotalSchemes(),
                profitPostingRepository.countByStatus(ProfitPostingStatus.POSTED),
                metrics,
                dashboardRecentItems
        );
    }

    public CalculationSimulationResponse simulate(CalculationSimulateRequest request) {
        validate(request);

        String sourceModule = normalize(request.sourceModule(), "GENERAL");
        String productType = request.productType().trim().toUpperCase(Locale.ROOT);
        BigDecimal principal = scaleMoney(request.principalAmount());
        BigDecimal rate = scaleRate(request.ratePercent());
        int tenureMonths = request.tenureMonths();
        ProfitFrequency frequency = parseFrequency(request.frequency());
        LocalDate startDate = request.startDate() == null ? LocalDate.now() : request.startDate();
        int monthSpan = frequency.getMonthSpan();
        int periods = (int) Math.ceil((double) tenureMonths / monthSpan);

        if (productType.endsWith("_SAVINGS")) {
            return buildSavingsProjection(sourceModule, productType, principal, rate, tenureMonths, frequency, startDate, periods);
        }
        if ("PROFIT_POSTING".equals(productType)) {
            return buildProfitPostingProjection(sourceModule, productType, principal, rate, tenureMonths, frequency, startDate, periods);
        }
        return switch (productType) {
            case "MURABAHA", "SALAM", "ISTISNA" -> buildMurabahaProjection(sourceModule, productType, principal, rate, tenureMonths, frequency, startDate, periods);
            case "IJARAH" -> buildIjarahProjection(sourceModule, productType, principal, rate, tenureMonths, frequency, startDate, periods);
            case "MUSHARAKA" -> buildMusharakaProjection(sourceModule, productType, principal, rate, tenureMonths, frequency, startDate, periods);
            case "MUDARABA" -> buildMudarabaProjection(sourceModule, productType, principal, rate, tenureMonths, frequency, startDate, periods);
            default -> throw new BadRequestException("Unsupported product type for calculation engine");
        };
    }

    private CalculationSimulationResponse buildMurabahaProjection(String sourceModule, String productType, BigDecimal principal,
                                                                  BigDecimal rate, int tenureMonths, ProfitFrequency frequency,
                                                                  LocalDate startDate, int periods) {
        BigDecimal totalProfit = scaleMoney(
                principal.multiply(rate)
                        .multiply(BigDecimal.valueOf(tenureMonths))
                        .divide(BigDecimal.valueOf(1200), 8, RoundingMode.HALF_UP)
        );
        BigDecimal totalPayable = scaleMoney(principal.add(totalProfit));
        BigDecimal principalPerPeriod = divideMoney(principal, periods);
        BigDecimal profitPerPeriod = divideMoney(totalProfit, periods);
        BigDecimal periodicAmount = scaleMoney(principalPerPeriod.add(profitPerPeriod));

        List<CalculationScheduleItemResponse> schedule = new ArrayList<>();
        BigDecimal outstanding = principal;
        for (int i = 1; i <= periods; i++) {
            BigDecimal periodPrincipal = adjustLast(principalPerPeriod, principal, i, periods, schedule.stream().map(CalculationScheduleItemResponse::principalComponent).reduce(BigDecimal.ZERO, BigDecimal::add));
            BigDecimal periodProfit = adjustLast(profitPerPeriod, totalProfit, i, periods, schedule.stream().map(CalculationScheduleItemResponse::profitComponent).reduce(BigDecimal.ZERO, BigDecimal::add));
            outstanding = scaleMoney(outstanding.subtract(periodPrincipal).max(BigDecimal.ZERO));
            schedule.add(new CalculationScheduleItemResponse(
                    i,
                    startDate.plusMonths((long) frequency.getMonthSpan() * i),
                    periodPrincipal,
                    periodProfit,
                    scaleMoney(periodPrincipal.add(periodProfit)),
                    outstanding,
                    "Cost plus markup installment"
            ));
        }

        return new CalculationSimulationResponse(
                sourceModule,
                productType,
                "Murabaha Cost Plus Formula",
                principal,
                rate,
                tenureMonths,
                frequency.name(),
                startDate,
                principal,
                totalProfit,
                totalPayable,
                periodicAmount,
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                List.of(
                        "Markup is distributed equally across the selected period count.",
                        "Schedule remains reproducible by tenure and frequency.",
                        "This projection is simulation-only and does not post to source tables."
                ),
                schedule
        );
    }

    private CalculationSimulationResponse buildIjarahProjection(String sourceModule, String productType, BigDecimal principal,
                                                                BigDecimal rate, int tenureMonths, ProfitFrequency frequency,
                                                                LocalDate startDate, int periods) {
        BigDecimal principalPerPeriod = divideMoney(principal, periods);
        BigDecimal rentalPerPeriod = scaleMoney(
                principal.multiply(rate)
                        .multiply(BigDecimal.valueOf(frequency.getMonthSpan()))
                        .divide(BigDecimal.valueOf(1200), 8, RoundingMode.HALF_UP)
        );
        BigDecimal totalProfit = scaleMoney(rentalPerPeriod.multiply(BigDecimal.valueOf(periods)));
        BigDecimal totalPayable = scaleMoney(principal.add(totalProfit));
        BigDecimal periodicAmount = scaleMoney(principalPerPeriod.add(rentalPerPeriod));

        List<CalculationScheduleItemResponse> schedule = new ArrayList<>();
        BigDecimal outstanding = principal;
        for (int i = 1; i <= periods; i++) {
            BigDecimal periodPrincipal = adjustLast(principalPerPeriod, principal, i, periods, schedule.stream().map(CalculationScheduleItemResponse::principalComponent).reduce(BigDecimal.ZERO, BigDecimal::add));
            outstanding = scaleMoney(outstanding.subtract(periodPrincipal).max(BigDecimal.ZERO));
            schedule.add(new CalculationScheduleItemResponse(
                    i,
                    startDate.plusMonths((long) frequency.getMonthSpan() * i),
                    periodPrincipal,
                    rentalPerPeriod,
                    scaleMoney(periodPrincipal.add(rentalPerPeriod)),
                    outstanding,
                    "Lease rental plus asset recovery"
            ));
        }

        return new CalculationSimulationResponse(
                sourceModule,
                productType,
                "Ijarah Rental Formula",
                principal,
                rate,
                tenureMonths,
                frequency.name(),
                startDate,
                principal,
                totalProfit,
                totalPayable,
                periodicAmount,
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                List.of(
                        "Rental component is linked to the asset value and selected frequency.",
                        "Principal recovery is spread across the lease horizon.",
                        "Use this preview before finalizing source-module schedule generation."
                ),
                schedule
        );
    }

    private CalculationSimulationResponse buildMusharakaProjection(String sourceModule, String productType, BigDecimal principal,
                                                                   BigDecimal rate, int tenureMonths, ProfitFrequency frequency,
                                                                   LocalDate startDate, int periods) {
        BigDecimal principalPerPeriod = divideMoney(principal, periods);
        List<CalculationScheduleItemResponse> schedule = new ArrayList<>();
        BigDecimal outstanding = principal;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (int i = 1; i <= periods; i++) {
            BigDecimal profit = scaleMoney(
                    outstanding.multiply(rate)
                            .multiply(BigDecimal.valueOf(frequency.getMonthSpan()))
                            .divide(BigDecimal.valueOf(1200), 8, RoundingMode.HALF_UP)
            );
            BigDecimal periodPrincipal = adjustLast(principalPerPeriod, principal, i, periods, schedule.stream().map(CalculationScheduleItemResponse::principalComponent).reduce(BigDecimal.ZERO, BigDecimal::add));
            outstanding = scaleMoney(outstanding.subtract(periodPrincipal).max(BigDecimal.ZERO));
            totalProfit = scaleMoney(totalProfit.add(profit));
            schedule.add(new CalculationScheduleItemResponse(
                    i,
                    startDate.plusMonths((long) frequency.getMonthSpan() * i),
                    periodPrincipal,
                    profit,
                    scaleMoney(periodPrincipal.add(profit)),
                    outstanding,
                    "Diminishing partnership share and expected profit"
            ));
        }

        BigDecimal totalPayable = scaleMoney(principal.add(totalProfit));
        return new CalculationSimulationResponse(
                sourceModule,
                productType,
                "Musharaka Diminishing Share Formula",
                principal,
                rate,
                tenureMonths,
                frequency.name(),
                startDate,
                principal,
                totalProfit,
                totalPayable,
                schedule.isEmpty() ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : schedule.get(0).totalAmount(),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                List.of(
                        "Outstanding capital declines each period before the next profit share is estimated.",
                        "Profit share is period-based and tied to Islamic partnership logic rather than flat interest.",
                        "Use actual business approval rules before posting this result into financing records."
                ),
                schedule
        );
    }

    private CalculationSimulationResponse buildMudarabaProjection(String sourceModule, String productType, BigDecimal principal,
                                                                  BigDecimal rate, int tenureMonths, ProfitFrequency frequency,
                                                                  LocalDate startDate, int periods) {
        List<CalculationScheduleItemResponse> schedule = new ArrayList<>();
        BigDecimal periodProfit = scaleMoney(
                principal.multiply(rate)
                        .multiply(BigDecimal.valueOf(frequency.getMonthSpan()))
                        .divide(BigDecimal.valueOf(1200), 8, RoundingMode.HALF_UP)
        );
        BigDecimal totalProfit = scaleMoney(periodProfit.multiply(BigDecimal.valueOf(periods)));
        BigDecimal totalPayable = scaleMoney(principal.add(totalProfit));

        for (int i = 1; i <= periods; i++) {
            BigDecimal principalComponent = i == periods ? principal : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal outstanding = i == periods ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : principal;
            schedule.add(new CalculationScheduleItemResponse(
                    i,
                    startDate.plusMonths((long) frequency.getMonthSpan() * i),
                    principalComponent,
                    periodProfit,
                    scaleMoney(principalComponent.add(periodProfit)),
                    outstanding,
                    i == periods ? "Capital settlement with final profit share" : "Projected mudaraba profit share only"
            ));
        }

        return new CalculationSimulationResponse(
                sourceModule,
                productType,
                "Mudaraba Profit Share Formula",
                principal,
                rate,
                tenureMonths,
                frequency.name(),
                startDate,
                principal,
                totalProfit,
                totalPayable,
                periodProfit,
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                List.of(
                        "Interim periods carry projected profit share while principal remains invested.",
                        "Principal is settled at maturity in this simulator profile.",
                        "Adjust expected share assumptions with real mudaraba policy before use."
                ),
                schedule
        );
    }

    private CalculationSimulationResponse buildSavingsProjection(String sourceModule, String productType, BigDecimal installment,
                                                                 BigDecimal rate, int tenureMonths, ProfitFrequency frequency,
                                                                 LocalDate startDate, int periods) {
        List<CalculationScheduleItemResponse> schedule = new ArrayList<>();
        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;
        BigDecimal outstanding = BigDecimal.ZERO;

        for (int i = 1; i <= periods; i++) {
            BigDecimal contribution = installment;
            outstanding = scaleMoney(outstanding.add(contribution));
            BigDecimal projectedProfit = scaleMoney(
                    outstanding.multiply(rate)
                            .multiply(BigDecimal.valueOf(frequency.getMonthSpan()))
                            .divide(BigDecimal.valueOf(1200), 8, RoundingMode.HALF_UP)
            );
            totalPrincipal = scaleMoney(totalPrincipal.add(contribution));
            totalProfit = scaleMoney(totalProfit.add(projectedProfit));
            schedule.add(new CalculationScheduleItemResponse(
                    i,
                    startDate.plusMonths((long) frequency.getMonthSpan() * (i - 1)),
                    contribution,
                    projectedProfit,
                    scaleMoney(contribution.add(projectedProfit)),
                    outstanding,
                    "Recurring savings contribution plus projected profit bucket"
            ));
        }

        return new CalculationSimulationResponse(
                sourceModule,
                productType,
                "Recurring Deposit Projection",
                installment,
                rate,
                tenureMonths,
                frequency.name(),
                startDate,
                totalPrincipal,
                totalProfit,
                scaleMoney(totalPrincipal.add(totalProfit)),
                scaleMoney(installment.add(schedule.isEmpty() ? BigDecimal.ZERO : schedule.get(0).profitComponent())),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                List.of(
                        "For savings products, principal input is treated as recurring installment amount.",
                        "Projected profit compounds on accumulated balance by selected frequency.",
                        "Source-module schedules remain the final authority for actual posting."
                ),
                schedule
        );
    }

    private CalculationSimulationResponse buildProfitPostingProjection(String sourceModule, String productType, BigDecimal averageBalance,
                                                                       BigDecimal rate, int tenureMonths, ProfitFrequency frequency,
                                                                       LocalDate startDate, int periods) {
        List<CalculationScheduleItemResponse> schedule = new ArrayList<>();
        BigDecimal periodsPerYear = BigDecimal.valueOf(12L / frequency.getMonthSpan());
        BigDecimal periodicProfit = scaleMoney(
                averageBalance.multiply(rate)
                        .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP)
                        .divide(periodsPerYear, 2, RoundingMode.HALF_UP)
        );
        BigDecimal totalProfit = scaleMoney(periodicProfit.multiply(BigDecimal.valueOf(periods)));

        for (int i = 1; i <= periods; i++) {
            schedule.add(new CalculationScheduleItemResponse(
                    i,
                    startDate.plusMonths((long) frequency.getMonthSpan() * i),
                    BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                    periodicProfit,
                    periodicProfit,
                    averageBalance,
                    "Projected profit posting on average balance"
            ));
        }

        return new CalculationSimulationResponse(
                sourceModule,
                productType,
                "Average Balance Profit Posting Formula",
                averageBalance,
                rate,
                tenureMonths,
                frequency.name(),
                startDate,
                averageBalance,
                totalProfit,
                totalProfit,
                periodicProfit,
                averageBalance,
                List.of(
                        "Principal input is treated as average eligible balance.",
                        "Profit is split by posting frequency rather than repayment installment.",
                        "Use active PSR ratio and snapshot rules before real posting."
                ),
                schedule
        );
    }

    private void validate(CalculationSimulateRequest request) {
        if (request == null) {
            throw new BadRequestException("Calculation request is required");
        }
        if (request.productType() == null || request.productType().trim().isEmpty()) {
            throw new BadRequestException("Product type is required");
        }
        if (request.principalAmount() == null || request.principalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Principal or base amount must be greater than zero");
        }
        if (request.ratePercent() == null || request.ratePercent().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Rate percent must be zero or greater");
        }
        if (request.tenureMonths() == null || request.tenureMonths() <= 0) {
            throw new BadRequestException("Tenure months must be greater than zero");
        }
        parseFrequency(request.frequency());
    }

    private ProfitFrequency parseFrequency(String value) {
        try {
            return ProfitFrequency.valueOf((value == null ? "MONTHLY" : value.trim().toUpperCase(Locale.ROOT)));
        } catch (Exception ex) {
            throw new BadRequestException("Valid frequency is required");
        }
    }

    private String normalize(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim().toUpperCase(Locale.ROOT);
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal scaleRate(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP) : value.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal divideMoney(BigDecimal amount, int divisor) {
        return scaleMoney(amount.divide(BigDecimal.valueOf(divisor), 8, RoundingMode.HALF_UP));
    }

    private BigDecimal adjustLast(BigDecimal baseValue, BigDecimal totalValue, int index, int totalPeriods, BigDecimal allocated) {
        if (index == totalPeriods) {
            return scaleMoney(totalValue.subtract(allocated));
        }
        return baseValue;
    }
}
