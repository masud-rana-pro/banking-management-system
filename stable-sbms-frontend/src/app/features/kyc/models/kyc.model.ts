export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH';
export type KycReviewStatus = 'DRAFT' | 'SUBMITTED' | 'UNDER_REVIEW' | 'VERIFIED' | 'APPROVED' | 'REJECTED' | 'SENT_BACK';
export type KycDocumentType =
  'NID' | 'PASSPORT' | 'BIRTH_CERTIFICATE' | 'TRADE_LICENSE' | 'TIN' |
  'DRIVING_LICENSE' | 'UTILITY_BILL' | 'BANK_STATEMENT' | 'PHOTO' | 'OTHER';
export type RecordStatus = 'ACTIVE' | 'PENDING' | 'ARCHIVED';

export interface KycProfileRequest {
  customerId: number | null;
  riskLevel: RiskLevel | '';
  sourceOfFundsNote: string;
  pepFlag: boolean;
  sanctionFlag: boolean;
  amlFlag: boolean;
  reviewStatus: KycReviewStatus | '';
  remarks: string;
  status: RecordStatus | '';
}

export interface KycProfileResponse extends KycProfileRequest {
  id: number;
  customerCode: string;
  customerName: string;
  branchId: number | null;
  customerStatus: string;
  documentCount?: number;
  decisionCount?: number;
  reviewedBy?: string;
  reviewedAt?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface KycDocumentRequest {
  customerId: number | null;
  documentType: KycDocumentType | '';
  fileReferenceId: string;
  documentNo: string;
  issueDate: string;
  expiryDate: string;
  verifiedFlag: boolean;
  status: RecordStatus | '';
}

export interface KycDocumentResponse extends KycDocumentRequest {
  id: number;
  customerCode: string;
  customerName: string;
  createdAt?: string;
}

export interface KycDecisionActionRequest {
  remarks: string;
}

export interface KycDecisionHistoryResponse {
  id: number;
  kycProfileId: number;
  decision: string;
  decisionBy: string;
  decisionAt?: string;
  remarks: string;
  status: RecordStatus;
}

export interface KycDashboardSummaryResponse {
  pendingKyc: number;
  verifiedKyc: number;
  rejectedKyc: number;
  resubmissionQueue: number;
  highRiskCustomers: number;
  lowRiskCount: number;
  mediumRiskCount: number;
  highRiskCount: number;
}

export interface OptionItem<T = string | number> {
  label: string;
  value: T;
}

export const RISK_LEVEL_OPTIONS: OptionItem<RiskLevel>[] = [
  { label: 'LOW', value: 'LOW' },
  { label: 'MEDIUM', value: 'MEDIUM' },
  { label: 'HIGH', value: 'HIGH' }
];

export const KYC_REVIEW_STATUS_OPTIONS: OptionItem<KycReviewStatus>[] = [
  { label: 'DRAFT', value: 'DRAFT' },
  { label: 'SUBMITTED', value: 'SUBMITTED' },
  { label: 'UNDER REVIEW', value: 'UNDER_REVIEW' },
  { label: 'VERIFIED', value: 'VERIFIED' },
  { label: 'APPROVED', value: 'APPROVED' },
  { label: 'REJECTED', value: 'REJECTED' },
  { label: 'SENT BACK', value: 'SENT_BACK' }
];

export const KYC_DOCUMENT_TYPE_OPTIONS: OptionItem<KycDocumentType>[] = [
  { label: 'NID', value: 'NID' },
  { label: 'PASSPORT', value: 'PASSPORT' },
  { label: 'BIRTH CERTIFICATE', value: 'BIRTH_CERTIFICATE' },
  { label: 'TRADE LICENSE', value: 'TRADE_LICENSE' },
  { label: 'TIN', value: 'TIN' },
  { label: 'DRIVING LICENSE', value: 'DRIVING_LICENSE' },
  { label: 'UTILITY BILL', value: 'UTILITY_BILL' },
  { label: 'BANK STATEMENT', value: 'BANK_STATEMENT' },
  { label: 'PHOTO', value: 'PHOTO' },
  { label: 'OTHER', value: 'OTHER' }
];

export const RECORD_STATUS_OPTIONS: OptionItem<RecordStatus>[] = [
  { label: 'ACTIVE', value: 'ACTIVE' },
  { label: 'PENDING', value: 'PENDING' },
  { label: 'ARCHIVED', value: 'ARCHIVED' }
];

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
