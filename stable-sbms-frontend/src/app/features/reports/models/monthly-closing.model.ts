export interface MonthlyClosingRunRequest {
  branchId: number | null;
  closingMonth: string;
  vaultClosedConfirmed: boolean;
  profitPostedConfirmed: boolean;
  reversalsReviewed: boolean;
  statementsGenerated: boolean;
  remarks?: string | null;
}

export interface MonthlyClosingDecisionRequest {
  remarks?: string | null;
}

export interface MonthlyClosingRunResponse {
  id: number;
  closingRef: string;
  branchId: number;
  branchCode?: string | null;
  branchName?: string | null;
  closingMonth: string;
  periodFrom?: string | null;
  periodTo?: string | null;
  transactionAmount?: number | null;
  reversedCount?: number | null;
  vaultClosingBalance?: number | null;
  profitPosted?: number | null;
  vaultClosedConfirmed?: boolean | null;
  profitPostedConfirmed?: boolean | null;
  reversalsReviewed?: boolean | null;
  statementsGenerated?: boolean | null;
  checklistCompletedCount?: number | null;
  checklistTotalCount?: number | null;
  checklistProgressPercent?: number | null;
  readyForSubmit?: boolean | null;
  outstandingChecklistItems?: string[] | null;
  status: string;
  remarks?: string | null;
  createdBy?: string | null;
  submittedBy?: string | null;
  submittedAt?: string | null;
  approvedBy?: string | null;
  approvedAt?: string | null;
  rejectedBy?: string | null;
  rejectedAt?: string | null;
  reopenedBy?: string | null;
  reopenedAt?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface MonthlyClosingDashboardSummaryResponse {
  draftCount: number;
  submittedCount: number;
  approvedCount: number;
  rejectedCount: number;
  reopenedCount: number;
}
