export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface GeneralDashboardResponse {
  scopeLabel: string;
  window: string;
  dateFrom: string;
  dateTo: string;
  kpis: DashboardKpiResponse[];
  businessTrend: DashboardTrendPointResponse[];
  profitabilityTrend: DashboardTrendPointResponse[];
  portfolioMix: DashboardMixResponse[];
  branchPerformance: DashboardBranchPerformanceResponse[];
  riskSnapshot: DashboardRiskResponse[];
  pendingApprovals: DashboardActivityResponse[];
  recentTransactions: DashboardActivityResponse[];
  alerts: DashboardActivityResponse[];
}

export interface DashboardKpiResponse {
  code: string;
  label: string;
  value: number;
  displayValue: string;
  helper: string;
  icon: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
  changePercent: number;
}

export interface DashboardTrendPointResponse {
  label: string;
  depositInflow: number;
  withdrawalOutflow: number;
  financingDisbursed: number;
  income: number;
  expense: number;
  netProfit: number;
  transactionVolume: number;
}

export interface DashboardMixResponse {
  label: string;
  value: number;
  percentage: number;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
}

export interface DashboardBranchPerformanceResponse {
  branchId: number | null;
  branchCode: string;
  branchName: string;
  deposits: number;
  financing: number;
  income: number;
  expense: number;
  netProfit: number;
  transactionVolume: number;
}

export interface DashboardRiskResponse {
  label: string;
  value: number;
  helper: string;
  route: string;
  icon: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
}

export interface DashboardActivityResponse {
  title: string;
  subtitle: string;
  badge: string;
  amount: number;
  route: string;
  icon: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
}

