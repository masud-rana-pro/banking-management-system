export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type ReportType = 'OPERATIONAL' | 'REGULATORY' | 'FINANCING' | 'PROFIT' | 'PAR' | 'SHARIAH_AUDIT' | 'BRANCH' | 'KPI' | 'GROWTH' | 'RECOVERY' | 'CLOSING';
export type ReportRequestStatus = 'REQUESTED' | 'GENERATED' | 'EXPORTED' | 'FAILED';
export type ReportExportType = 'PDF' | 'CSV' | 'EXCEL' | 'PRINT';
export type ReportQueryKey = 'OPERATIONAL' | 'PROFIT_DISTRIBUTION' | 'MANAGEMENT_PL' | 'TRIAL_BALANCE' | 'LEDGER_PROFIT_LOSS' | 'FINANCING_PORTFOLIO' | 'PAR' | 'SHARIAH_AUDIT' | 'BRANCH' | 'KPI' | 'GROWTH' | 'LOAN_RECOVERY' | 'MONTHLY_CLOSING';

export interface ReportColumnResponse {
  key: string;
  label: string;
}

export interface ReportMetricResponse {
  label: string;
  value: string;
  tone: string;
}

export interface ReportFileResponse {
  id: number;
  fileName: string;
  originalFileName: string;
  filePath: string;
  fileType: string;
  fileSize: number;
}

export interface ReportRequestLogResponse {
  id: number;
  reportId: number;
  reportCode: string;
  reportName: string;
  reportType: ReportType;
  queryKey: ReportQueryKey;
  requestedBy: string;
  dateFrom?: string | null;
  dateTo?: string | null;
  filterJson?: string | null;
  generatedFile?: ReportFileResponse | null;
  requestStatus: ReportRequestStatus;
  requestedAt?: string;
  generatedAt?: string | null;
  status: string;
}

export interface ReportUsageSummaryResponse {
  reportCode: string;
  reportName: string;
  queryKey: ReportQueryKey;
  usageCount: number;
}

export interface ReportDashboardSummaryResponse {
  generatedToday: number;
  regulatoryPending: number;
  branchPerformanceSummary: number;
  financingSummary: number;
  profitSummary: number;
  mostUsedReports: ReportUsageSummaryResponse[];
  recentExports: ReportRequestLogResponse[];
}

export interface ReportResultResponse {
  logId: number;
  reportId: number;
  reportCode: string;
  reportName: string;
  reportType: ReportType;
  queryKey: ReportQueryKey;
  dateFrom: string;
  dateTo: string;
  branchId?: number | null;
  exportType?: ReportExportType | null;
  requestedBy: string;
  filterJson?: string | null;
  runStatus: ReportRequestStatus;
  generatedAt?: string | null;
  generatedFile?: ReportFileResponse | null;
  metrics: ReportMetricResponse[];
  columns: ReportColumnResponse[];
  rows: Record<string, unknown>[];
  exportHistory: ReportRequestLogResponse[];
}

export interface ReportPageConfig {
  title: string;
  subtitle: string;
  currentLabel: string;
  queryKey: ReportQueryKey;
  route: string;
  homeRoute: string;
  icon: string;
  enableBranchFilter?: boolean;
}

export interface ManagementExpenseEntryRequest {
  expenseDate: string;
  branchId?: number | null;
  expenseCategory: string;
  expenseCode?: string | null;
  amount: number;
  referenceNo?: string | null;
  remarks?: string | null;
}

export interface ManagementExpenseEntryResponse {
  id: number;
  expenseDate: string;
  branchId?: number | null;
  branchCode?: string | null;
  branchName?: string | null;
  expenseCategory: string;
  expenseCode?: string | null;
  amount: number;
  sourceType: string;
  referenceNo?: string | null;
  remarks?: string | null;
  createdBy?: string | null;
  createdAt?: string | null;
}

export interface AccountingFilters {
  dateFrom?: string;
  dateTo?: string;
  branchId?: number | null;
}

export interface TrialBalanceRowResponse {
  accountCode: string;
  accountName: string;
  accountType: string;
  totalDebit: number;
  totalCredit: number;
  netBalance: number;
}

export interface TrialBalanceResponse {
  dateFrom?: string | null;
  dateTo?: string | null;
  branchId?: number | null;
  totalDebit: number;
  totalCredit: number;
  rowCount: number;
  rows: TrialBalanceRowResponse[];
}

export interface GlJournalLineResponse {
  id: number;
  lineNo: number;
  accountCode: string;
  entrySide: string;
  amount: number;
  remarks?: string | null;
}

export interface GlJournalResponse {
  id: number;
  journalDate: string;
  journalType: string;
  sourceType: string;
  sourceReferenceId?: number | null;
  sourceReferenceNo?: string | null;
  branchId?: number | null;
  description?: string | null;
  status: string;
  createdBy?: string | null;
  createdAt?: string | null;
  lines: GlJournalLineResponse[];
}

export interface ProfitLossRowResponse {
  accountCode: string;
  accountName: string;
  amount: number;
}

export interface ProfitLossBranchSummaryResponse {
  branchId?: number | null;
  branchCode?: string | null;
  branchName?: string | null;
  totalIncome: number;
  totalExpense: number;
  netProfit: number;
}

export interface ProfitLossResponse {
  dateFrom?: string | null;
  dateTo?: string | null;
  branchId?: number | null;
  totalIncome: number;
  totalExpense: number;
  netProfit: number;
  incomeRows: ProfitLossRowResponse[];
  expenseRows: ProfitLossRowResponse[];
  branchSummaries: ProfitLossBranchSummaryResponse[];
}

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
