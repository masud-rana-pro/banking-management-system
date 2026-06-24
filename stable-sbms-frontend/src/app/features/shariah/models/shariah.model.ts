export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type RecordStatus = 'ACTIVE' | 'PENDING' | 'ARCHIVED';
export type ShariahCaseStatus = 'PENDING_REVIEW' | 'APPROVED' | 'REJECTED' | 'RETURNED';
export type ShariahDecisionType = 'SUBMITTED' | 'CHECKLIST_UPDATED' | 'APPROVED' | 'REJECTED' | 'RETURNED';

export interface OptionItem<T = string | number> {
  label: string;
  value: T;
}

export interface ShariahChecklistSelectionRequest {
  itemId: number | null;
  selected: boolean;
  note: string;
}

export interface ShariahReviewCaseRequest {
  referenceModule: string;
  referenceId: number | null;
  submittedBy: string;
  remarks: string;
}

export interface ShariahChecklistSaveRequest {
  reviewedBy: string;
  remarks: string;
  checklistItems: ShariahChecklistSelectionRequest[];
}

export interface ShariahDecisionRequest {
  decisionBy: string;
  remarks: string;
  checklistItems: ShariahChecklistSelectionRequest[];
}

export interface ShariahChecklistItemResponse {
  id: number;
  itemCode: string;
  itemName: string;
  description?: string;
  status: RecordStatus;
  selected: boolean;
  note?: string;
  createdAt?: string;
}

export interface ShariahReviewDecisionResponse {
  id: number;
  caseId: number;
  caseNo: string;
  decision: ShariahDecisionType;
  decisionBy: string;
  decisionAt?: string;
  remarks?: string;
  status: RecordStatus;
  createdAt?: string;
}

export interface ShariahReviewCaseResponse {
  id: number;
  caseNo: string;
  referenceModule: string;
  referenceId: number;
  submittedBy: string;
  submittedAt?: string;
  caseStatus: ShariahCaseStatus;
  remarks?: string;
  status: RecordStatus;
  createdAt?: string;
  updatedAt?: string;
  checklistItems: ShariahChecklistItemResponse[];
  history: ShariahReviewDecisionResponse[];
}

export interface ShariahModuleSummaryResponse {
  referenceModule: string;
  totalCases: number;
}

export interface ShariahDashboardSummaryResponse {
  pendingCases: number;
  approvedCases: number;
  rejectedCases: number;
  correctionRequests: number;
  upcomingReviews: number;
  recentCases: ShariahReviewCaseResponse[];
  recentDecisions: ShariahReviewDecisionResponse[];
  moduleBreakdown: ShariahModuleSummaryResponse[];
}

export const REFERENCE_MODULE_OPTIONS: OptionItem<string>[] = [
  { label: 'FINANCING', value: 'FINANCING' },
  { label: 'CONTRACT', value: 'CONTRACT' },
  { label: 'ACCOUNT OPENING', value: 'ACCOUNT_OPENING' },
  { label: 'DEPOSIT SCHEME', value: 'DEPOSIT_SCHEME' },
  { label: 'CARD ISSUE', value: 'CARD_ISSUE' },
  { label: 'GENERAL', value: 'GENERAL' }
];

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
