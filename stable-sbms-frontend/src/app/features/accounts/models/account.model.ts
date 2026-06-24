export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type RecordStatus = 'ACTIVE' | 'PENDING' | 'ARCHIVED';
export type ShariahContractType = 'MUDARABAH' | 'WADIAH' | 'QARD' | 'IJARAH' | 'MURABAHA';
export type AccountOpeningRequestStatus = 'DRAFT' | 'SUBMITTED' | 'VERIFIED' | 'APPROVED' | 'REJECTED' | 'SENT_BACK';
export type AccountStatus = 'PENDING_ACTIVATION' | 'ACTIVE' | 'BLOCKED' | 'FROZEN' | 'CLOSED';

export interface OptionItem<T = string | number> {
  label: string;
  value: T;
}

export interface AccountTypeRequest {
  typeCode: string;
  typeName: string;
  shariahContractType: ShariahContractType | '';
  currencyCode: string;
  minimumOpeningBalance: number | null;
  profitApplicable: boolean;
  withdrawalAllowed: boolean;
  status: RecordStatus | '';
}

export interface AccountTypeResponse extends AccountTypeRequest {
  id: number;
  createdAt?: string;
}

export interface AccountTypeDropdownResponse {
  id: number;
  typeCode: string;
  typeName: string;
  displayName: string;
}

export interface AccountOpeningRequestRequest {
  customerId: number | null;
  accountTypeId: number | null;
  branchId: number | null;
  requestedDate: string;
  initialDepositAmount: number | null;
  requestStatus: AccountOpeningRequestStatus | '';
  remarks: string;
  applicantImageName: string;
  status: RecordStatus | '';
}

export interface AccountOpeningRequestResponse extends AccountOpeningRequestRequest {
  id: number;
  requestNo: string;
  customerCode: string;
  customerName: string;
  accountTypeCode: string;
  accountTypeName: string;
  verifiedBy?: string;
  verifiedAt?: string;
  approvedBy?: string;
  approvedAt?: string;
  createdAt?: string;
  accountId?: number | null;
}

export interface AccountResponse {
  id: number;
  accountNumber: string;
  customerId: number;
  customerCode: string;
  customerName: string;
  accountTypeId: number;
  accountTypeCode: string;
  accountTypeName: string;
  branchId: number | null;
  openingRequestId?: number | null;
  requestNo?: string | null;
  openedDate?: string;
  currentBalance: number;
  availableBalance: number;
  profitRatioId?: number | null;
  accountStatus: AccountStatus;
  closedDate?: string;
  remarks: string;
  status: RecordStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface AccountDashboardSummaryResponse {
  totalAccounts: number;
  pendingOpeningRequests: number;
  activeAccounts: number;
  blockedAccounts: number;
  frozenAccounts: number;
  awaitingVerificationAccounts: number;
}

export interface AccountWorkflowActionRequest {
  remarks: string;
}

export const RECORD_STATUS_OPTIONS: OptionItem<RecordStatus>[] = [
  { label: 'ACTIVE', value: 'ACTIVE' },
  { label: 'PENDING', value: 'PENDING' },
  { label: 'ARCHIVED', value: 'ARCHIVED' }
];

export const SHARIAH_CONTRACT_OPTIONS: OptionItem<ShariahContractType>[] = [
  { label: 'MUDARABAH', value: 'MUDARABAH' },
  { label: 'WADIAH', value: 'WADIAH' },
  { label: 'QARD', value: 'QARD' },
  { label: 'IJARAH', value: 'IJARAH' },
  { label: 'MURABAHA', value: 'MURABAHA' }
];

export const ACCOUNT_OPENING_STATUS_OPTIONS: OptionItem<AccountOpeningRequestStatus>[] = [
  { label: 'DRAFT', value: 'DRAFT' },
  { label: 'SUBMITTED', value: 'SUBMITTED' },
  { label: 'VERIFIED', value: 'VERIFIED' },
  { label: 'APPROVED', value: 'APPROVED' },
  { label: 'REJECTED', value: 'REJECTED' },
  { label: 'SENT BACK', value: 'SENT_BACK' }
];

export const ACCOUNT_STATUS_OPTIONS: OptionItem<AccountStatus>[] = [
  { label: 'PENDING ACTIVATION', value: 'PENDING_ACTIVATION' },
  { label: 'ACTIVE', value: 'ACTIVE' },
  { label: 'BLOCKED', value: 'BLOCKED' },
  { label: 'FROZEN', value: 'FROZEN' },
  { label: 'CLOSED', value: 'CLOSED' }
];

export const CURRENCY_OPTIONS: OptionItem<string>[] = [
  { label: 'BDT', value: 'BDT' },
  { label: 'USD', value: 'USD' },
  { label: 'EUR', value: 'EUR' },
  { label: 'SAR', value: 'SAR' },
  { label: 'AED', value: 'AED' }
];

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
