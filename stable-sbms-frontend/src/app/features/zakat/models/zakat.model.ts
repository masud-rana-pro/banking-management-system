export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type RecordStatus = 'ACTIVE' | 'PENDING' | 'ARCHIVED';
export type ZakatCalculationStatus = 'PROFILED' | 'BELOW_NISAB' | 'CALCULATED' | 'DEDUCTED';
export type CharityFundSourceType = 'ZAKAT_DEDUCTION' | 'LATE_FEE' | 'DONATION' | 'PAYOUT';

export interface ZakatProfileRequest {
  customerId: number | null;
  zakatYear: number | null;
  nisabAmount: number | null;
  eligibleAssetAmount: number | null;
  remarks: string;
  proofDocumentName?: string | null;
}

export interface ZakatCalculationRequest {
  profileId: number | null;
  nisabAmount: number | null;
  eligibleAssetAmount: number | null;
  remarks: string;
}

export interface CharityBeneficiaryRequest {
  beneficiaryCode: string;
  beneficiaryName: string;
  mobile: string;
  address: string;
  proofDocumentName?: string | null;
}

export interface CharityPayoutRequest {
  beneficiaryId: number | null;
  payoutDate: string;
  amount: number | null;
  approvedBy: string;
  remarks: string;
}

export interface ZakatProfileResponse {
  id: number;
  customerId: number;
  customerCode: string;
  customerName: string;
  zakatYear: number;
  nisabAmount: number;
  eligibleAssetAmount: number;
  zakatAmount: number;
  calculationStatus: ZakatCalculationStatus;
  remarks?: string;
  proofDocumentName?: string | null;
  createdAt?: string;
  updatedAt?: string;
}

export interface CharityFundResponse {
  id: number;
  fundDate: string;
  sourceType: CharityFundSourceType;
  referenceId?: number;
  creditAmount: number;
  debitAmount: number;
  balanceAfter: number;
  remarks?: string;
  createdAt?: string;
}

export interface CharityBeneficiaryResponse {
  id: number;
  beneficiaryCode: string;
  beneficiaryName: string;
  mobile?: string;
  address?: string;
  proofDocumentName?: string | null;
  status: RecordStatus;
  createdAt?: string;
  updatedAt?: string;
  payoutCount: number;
}

export interface CharityPayoutResponse {
  id: number;
  beneficiaryId: number;
  beneficiaryCode: string;
  beneficiaryName: string;
  payoutDate: string;
  amount: number;
  approvedBy: string;
  remarks?: string;
  status: RecordStatus;
  createdAt?: string;
}

export interface ZakatDashboardSummaryResponse {
  zakatDueAccounts: number;
  totalZakatCalculated: number;
  charityFundBalance: number;
  beneficiaryPayoutTotal: number;
  upcomingZakatReminders: number;
  recentProfiles: ZakatProfileResponse[];
  recentFundMovements: CharityFundResponse[];
  recentPayouts: CharityPayoutResponse[];
}

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
