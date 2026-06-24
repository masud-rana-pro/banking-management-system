export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type RecordStatus = 'ACTIVE' | 'PENDING' | 'ARCHIVED';
export type FinancingType = 'MURABAHA' | 'IJARAH' | 'MUSHARAKA' | 'MUDARABA' | 'SALAM' | 'ISTISNA';
export type FinancingApplicationStatus =
  | 'DRAFT'
  | 'SUBMITTED'
  | 'DOC_CHECK'
  | 'ASSET_VERIFIED'
  | 'SHARIAH_REVIEW'
  | 'APPROVED'
  | 'REJECTED'
  | 'RETURNED'
  | 'DISBURSED'
  | 'ACTIVE'
  | 'CLOSED';
export type FinancingScheduleStatus = 'PENDING' | 'PARTIAL' | 'PAID' | 'OVERDUE';

export interface OptionItem<T = string | number> {
  label: string;
  value: T;
}

export interface FinancingProductRequest {
  productCode: string;
  productName: string;
  financingType: FinancingType | '';
  minimumAmount: number | null;
  maximumAmount: number | null;
  tenureMonths: number | null;
  profitRule: string;
}

export interface FinancingProductResponse {
  id: number;
  productCode: string;
  productName: string;
  financingType: FinancingType;
  minimumAmount: number;
  maximumAmount: number;
  tenureMonths: number;
  profitRule: string;
  status: RecordStatus;
  createdAt?: string;
  updatedAt?: string;
  applicationCount: number;
}

export interface FinancingApplicationRequest {
  customerId: number | null;
  productId: number | null;
  branchId: number | null;
  requestedAmount: number | null;
  assetDescription: string;
  purpose: string;
  supportingDocumentName?: string;
  remarks: string;
}

export interface FinancingWorkflowActionRequest {
  remarks: string;
  performedBy?: string;
}

export interface FinancingVerifyRequest {
  assetValue: number | null;
  verificationNote: string;
  verifiedBy: string;
  remarks: string;
}

export interface FinancingDisbursementRequest {
  disbursementDate: string;
  disbursedAmount: number | null;
  creditedAccountId: number | null;
  disbursedBy: string;
  remarks: string;
}

export interface FinancingRepaymentCollectionRequest {
  paymentAmount: number | null;
  paymentDate: string;
  remarks: string;
  collectedBy: string;
}

export interface FinancingAssetVerificationResponse {
  id: number;
  assetValue: number;
  verificationNote: string;
  verifiedBy: string;
  verifiedAt?: string;
  status: RecordStatus;
  createdAt?: string;
}

export interface FinancingDisbursementResponse {
  id: number;
  disbursementNo: string;
  disbursementDate: string;
  disbursedAmount: number;
  creditedAccountId: number;
  creditedAccountNumber?: string;
  disbursedBy: string;
  status: RecordStatus;
  createdAt?: string;
}

export interface FinancingScheduleResponse {
  id: number;
  applicationId: number;
  applicationNo: string;
  installmentNo: number;
  dueDate: string;
  principalAmount: number;
  profitAmount: number;
  charityAmount: number;
  paidAmount: number;
  paidDate?: string;
  scheduleStatus: FinancingScheduleStatus;
  createdAt?: string;
}

export interface FinancingApplicationResponse {
  id: number;
  applicationNo: string;
  customerId: number;
  customerCode: string;
  customerName: string;
  productId: number;
  productCode: string;
  productName: string;
  financingType: FinancingType;
  branchId: number;
  requestedAmount: number;
  assetDescription: string;
  purpose: string;
  supportingDocumentName?: string;
  applicationStatus: FinancingApplicationStatus;
  submittedAt?: string;
  approvedBy?: string;
  approvedAt?: string;
  remarks?: string;
  status: RecordStatus;
  createdAt?: string;
  updatedAt?: string;
  assetVerification?: FinancingAssetVerificationResponse | null;
  disbursement?: FinancingDisbursementResponse | null;
  schedules: FinancingScheduleResponse[];
  totalPaidAmount: number;
  totalOutstandingAmount: number;
  totalCharityAmount: number;
}

export interface FinancingProductMetricResponse {
  productName: string;
  applicationCount: number;
}

export interface FinancingDashboardSummaryResponse {
  pendingApplications: number;
  approvedApplications: number;
  disbursedAmount: number;
  overdueInstallments: number;
  financingByProduct: FinancingProductMetricResponse[];
  charityLateFeeAmount: number;
  recentApplications: FinancingApplicationResponse[];
}

export interface FinancingRepaymentCollectionResponse {
  applicationId: number;
  applicationNo: string;
  collectedAmount: number;
  remainingOutstandingAmount: number;
  applicationStatus: FinancingApplicationStatus;
  updatedSchedules: FinancingScheduleResponse[];
}

export interface CustomerOption {
  id: number;
  customerCode: string;
  fullName: string;
  displayName?: string;
}

export interface BranchOption {
  id: number;
  branchCode: string;
  branchName: string;
}

export interface AccountOption {
  id: number;
  accountNumber: string;
  customerId: number;
  customerCode: string;
  customerName: string;
  availableBalance: number;
  accountStatus: string;
}

export const FINANCING_TYPE_OPTIONS: OptionItem<FinancingType>[] = [
  { label: 'MURABAHA', value: 'MURABAHA' },
  { label: 'IJARAH', value: 'IJARAH' },
  { label: 'MUSHARAKA', value: 'MUSHARAKA' },
  { label: 'MUDARABA', value: 'MUDARABA' },
  { label: 'SALAM', value: 'SALAM' },
  { label: 'ISTISNA', value: 'ISTISNA' }
];

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
