export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type RecordStatus = 'ACTIVE' | 'PENDING' | 'ARCHIVED';
export type ProfitFrequency = 'MONTHLY' | 'QUARTERLY' | 'HALF_YEARLY' | 'YEARLY';
export type DepositSchemeType =
  | 'MONTHLY_SAVINGS'
  | 'HAJJ_SAVINGS'
  | 'EDUCATION_SAVINGS'
  | 'MARRIAGE_SAVINGS'
  | 'PENSION_SAVINGS'
  | 'GENERAL_SAVINGS';
export type DepositEnrollmentStatus =
  | 'ACTIVE'
  | 'MATURED'
  | 'EARLY_WITHDRAWAL_REQUESTED'
  | 'EARLY_WITHDRAWN'
  | 'CLOSED';
export type DepositSchedulePaymentStatus = 'PENDING' | 'PAID' | 'OVERDUE';
export type ProfitDistributionStatus = 'PENDING' | 'DISTRIBUTED' | 'SKIPPED';

export interface DepositSchemeRequest {
  schemeCode: string;
  schemeName: string;
  schemeType: DepositSchemeType | '';
  tenureMonths: number | null;
  minimumInstallment: number | null;
  profitRatio: number | null;
  profitFrequency: ProfitFrequency | '';
}

export interface DepositSchemeResponse {
  id: number;
  schemeCode: string;
  schemeName: string;
  schemeType: DepositSchemeType;
  tenureMonths: number;
  minimumInstallment: number;
  profitRatio: number;
  profitFrequency: ProfitFrequency;
  status: RecordStatus;
  createdAt?: string;
  updatedAt?: string;
  activeEnrollmentCount: number;
  maturedEnrollmentCount: number;
}

export interface DepositSchemeEnrollmentRequest {
  schemeId: number | null;
  customerId: number | null;
  linkedAccountId: number | null;
  startDate: string;
  installmentAmount: number | null;
  remarks: string;
}

export interface DepositSchemeEnrollmentResponse {
  id: number;
  enrollmentNo: string;
  schemeId: number;
  schemeCode: string;
  schemeName: string;
  schemeType: DepositSchemeType;
  customerId: number;
  customerCode: string;
  customerName: string;
  linkedAccountId: number;
  linkedAccountNumber: string;
  branchId: number | null;
  startDate: string;
  installmentAmount: number;
  maturityDate: string;
  enrollmentStatus: DepositEnrollmentStatus;
  maturityAmount: number;
  earlyWithdrawalRequested: boolean;
  earlyWithdrawalRequestedAt?: string;
  remarks?: string;
  status: RecordStatus;
  tenureMonths: number;
  schemeProfitRatio: number;
  schemeProfitFrequency: ProfitFrequency;
  totalScheduledInstallments: number;
  paidInstallments: number;
  remainingInstallments: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface DepositSchemeScheduleResponse {
  id: number;
  enrollmentId: number;
  enrollmentNo: string;
  installmentNo: number;
  dueDate: string;
  installmentAmount: number;
  profitAmount: number;
  totalDueAmount: number;
  paymentStatus: DepositSchedulePaymentStatus;
  paidAt?: string;
  status: RecordStatus;
  createdAt?: string;
}

export interface DepositSchemeProfitDistributionResponse {
  id: number;
  enrollmentId: number;
  enrollmentNo: string;
  distributionNo: number;
  periodFrom: string;
  periodTo: string;
  distributionDate: string;
  profitAmount: number;
  distributionStatus: ProfitDistributionStatus;
  creditedAccountId?: number;
  creditedAccountNumber?: string;
  remarks?: string;
  status: RecordStatus;
  createdAt?: string;
}

export interface DepositSchemeDashboardSummaryResponse {
  totalSchemes: number;
  activeEnrollments: number;
  dueInstallments: number;
  earlyWithdrawalRequests: number;
  maturedSchemes: number;
  recentSchemes: DepositSchemeResponse[];
  recentEnrollments: DepositSchemeEnrollmentResponse[];
}

export interface OptionItem<T = string | number> {
  label: string;
  value: T;
}

export const DEPOSIT_SCHEME_TYPE_OPTIONS: OptionItem<DepositSchemeType>[] = [
  { label: 'MONTHLY SAVINGS', value: 'MONTHLY_SAVINGS' },
  { label: 'HAJJ SAVINGS', value: 'HAJJ_SAVINGS' },
  { label: 'EDUCATION SAVINGS', value: 'EDUCATION_SAVINGS' },
  { label: 'MARRIAGE SAVINGS', value: 'MARRIAGE_SAVINGS' },
  { label: 'PENSION SAVINGS', value: 'PENSION_SAVINGS' },
  { label: 'GENERAL SAVINGS', value: 'GENERAL_SAVINGS' }
];

export const PROFIT_FREQUENCY_OPTIONS: OptionItem<ProfitFrequency>[] = [
  { label: 'MONTHLY', value: 'MONTHLY' },
  { label: 'QUARTERLY', value: 'QUARTERLY' },
  { label: 'HALF YEARLY', value: 'HALF_YEARLY' },
  { label: 'YEARLY', value: 'YEARLY' }
];

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
