export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type RecordStatus = 'ACTIVE' | 'PENDING' | 'ARCHIVED';
export type StatementRequestStatus = 'REQUESTED' | 'GENERATED' | 'DOWNLOADED' | 'FAILED';

export interface FileReferenceResponse {
  id: number;
  fileName: string;
  originalFileName: string;
  filePath: string;
  fileType: string;
  fileSize: number;
  moduleName: string;
  referenceTable: string;
  referenceId: number;
  status: RecordStatus;
  createdAt?: string;
}

export interface StatementLineResponse {
  lineDate: string;
  lineDateTime?: string;
  lineType: string;
  referenceNo: string;
  channel: string;
  debitAmount: number;
  creditAmount: number;
  narration: string;
}

export interface StatementMetricResponse {
  label: string;
  count: number;
}

export interface CustomerStatementRequestRequest {
  customerId: number | null;
  accountId: number | null;
  dateFrom: string;
  dateTo: string;
  requestedBy: string;
}

export interface BranchStatementRequestRequest {
  branchId: number | null;
  dateFrom: string;
  dateTo: string;
  requestedBy: string;
}

export interface CustomerStatementRequestResponse {
  id: number;
  requestNo: string;
  customerId: number;
  customerCode: string;
  customerName: string;
  accountId: number;
  accountNumber: string;
  branchId: number | null;
  dateFrom: string;
  dateTo: string;
  requestStatus: StatementRequestStatus;
  generatedFileId?: number | null;
  requestedBy: string;
  requestedAt?: string;
  generatedAt?: string;
  status: RecordStatus;
  generatedFile?: FileReferenceResponse | null;
  transactionCount: number;
  totalDebit: number;
  totalCredit: number;
  netMovement: number;
  profitPosted: number;
  currentBalance: number;
  lines: StatementLineResponse[];
}

export interface BranchStatementRequestResponse {
  id: number;
  requestNo: string;
  branchId: number;
  branchCode: string;
  branchName: string;
  dateFrom: string;
  dateTo: string;
  requestStatus: StatementRequestStatus;
  generatedFileId?: number | null;
  requestedBy: string;
  requestedAt?: string;
  generatedAt?: string;
  status: RecordStatus;
  generatedFile?: FileReferenceResponse | null;
  transactionCount: number;
  totalTransactionAmount: number;
  totalCashIn: number;
  totalCashOut: number;
  vaultClosingBalance: number;
  activeTerminalCount: number;
  lines: StatementLineResponse[];
}

export interface StatementDashboardSummaryResponse {
  statementsGeneratedToday: number;
  customerStatementRequests: number;
  branchStatementRequests: number;
  exportDownloadCounts: number;
  mostRequestedStatementTypes: StatementMetricResponse[];
  recentCustomerRequests: CustomerStatementRequestResponse[];
  recentBranchRequests: BranchStatementRequestResponse[];
}

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
