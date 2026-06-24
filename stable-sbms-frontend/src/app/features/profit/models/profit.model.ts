export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type RecordStatus = 'ACTIVE' | 'PENDING' | 'ARCHIVED';
export type ProfitFrequency = 'MONTHLY' | 'QUARTERLY' | 'HALF_YEARLY' | 'YEARLY';
export type ProfitPostingStatus = 'PENDING' | 'POSTED' | 'FAILED';

export interface OptionItem<T = string | number> {
  label: string;
  value: T;
}

export interface ProfitRatioRequest {
  ratioCode: string;
  accountTypeId: number | null;
  effectiveFrom: string;
  effectiveTo: string;
  ratioPercent: number | null;
}

export interface ProfitScheduleRequest {
  accountId: number | null;
  profitFrequency: ProfitFrequency | '';
  nextPostingDate: string;
}

export interface ProfitPostingRunRequest {
  scheduleId: number | null;
  accountId: number | null;
  postingDate: string;
  postedBy: string;
}

export interface ProfitRatioDropdownResponse {
  id: number;
  ratioCode: string;
  accountTypeId: number;
  accountTypeName: string;
}

export interface ProfitRatioResponse {
  id: number;
  ratioCode: string;
  accountTypeId: number;
  accountTypeCode: string;
  accountTypeName: string;
  effectiveFrom: string;
  effectiveTo: string | null;
  ratioPercent: number;
  status: RecordStatus;
  activeNow: boolean;
  linkedScheduleCount: number;
  linkedPostingCount: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface ProfitScheduleResponse {
  id: number;
  accountId: number;
  accountNumber: string;
  customerId: number;
  customerCode: string;
  customerName: string;
  accountTypeId: number;
  accountTypeCode: string;
  accountTypeName: string;
  branchId: number | null;
  currentBalance: number;
  profitFrequency: ProfitFrequency;
  nextPostingDate: string;
  lastPostingDate: string | null;
  status: RecordStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface ProfitPostingResponse {
  id: number;
  postingRef: string;
  accountId: number;
  accountNumber: string;
  customerId: number;
  customerCode: string;
  customerName: string;
  branchId: number | null;
  scheduleId: number;
  profitFrequency: ProfitFrequency;
  ratioCode: string | null;
  postingDate: string;
  profitAmount: number;
  periodFrom: string;
  periodTo: string;
  snapshotBalance: number | null;
  snapshotAverageBalance: number | null;
  postedBy: string | null;
  status: ProfitPostingStatus;
  failureReason: string | null;
  createdAt?: string;
}

export interface ProfitPostingRunResponse {
  processedCount: number;
  postedCount: number;
  failedCount: number;
  postings: ProfitPostingResponse[];
}

export interface UpcomingPostingRunResponse {
  nextPostingDate: string | null;
  pendingSchedules: number;
}

export interface ProfitDashboardSummaryResponse {
  activeProfitRatios: number;
  pendingPostingCycles: number;
  postedThisMonth: number;
  failedPostingLogs: number;
  currentPsrTable: ProfitRatioResponse[];
  upcomingPostingRun: UpcomingPostingRunResponse;
  recentFailedPostings: ProfitPostingResponse[];
}

export const PROFIT_FREQUENCY_OPTIONS: OptionItem<ProfitFrequency>[] = [
  { label: 'MONTHLY', value: 'MONTHLY' },
  { label: 'QUARTERLY', value: 'QUARTERLY' },
  { label: 'HALF YEARLY', value: 'HALF_YEARLY' },
  { label: 'YEARLY', value: 'YEARLY' }
];

export const RECORD_STATUS_OPTIONS: OptionItem<RecordStatus>[] = [
  { label: 'ACTIVE', value: 'ACTIVE' },
  { label: 'PENDING', value: 'PENDING' },
  { label: 'ARCHIVED', value: 'ARCHIVED' }
];

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
