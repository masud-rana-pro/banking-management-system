export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type RecordStatus = 'ACTIVE' | 'PENDING' | 'ARCHIVED';
export type TransactionType = 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER' | 'CHEQUE_CLEARING' | 'STANDING_INSTRUCTION' | 'REVERSAL';
export type ChannelType = 'BRANCH_COUNTER' | 'CHEQUE_COUNTER' | 'INTERNAL_TRANSFER' | 'SCHEDULED' | 'SYSTEM';
export type TransactionStatus = 'POSTED' | 'PENDING_REVIEW' | 'REVERSED' | 'FAILED';
export type CashType = 'CASH' | 'CHEQUE';
export type CashDirection = 'IN' | 'OUT';
export type TransferMode = 'INTERNAL' | 'BEFTN' | 'RTGS';
export type ReversalStatus = 'PENDING' | 'APPROVED' | 'REJECTED';
export type StandingInstructionStatus = 'ACTIVE' | 'PAUSED' | 'EXECUTED' | 'CANCELLED';

export interface OptionItem<T = string | number> {
  label: string;
  value: T;
}

export interface DepositRequest {
  branchId: number | null;
  terminalId: number | null;
  creditAccountId: number | null;
  tellerUserId: number | null;
  amount: number | null;
  narration: string;
  transactionDate: string;
}

export interface WithdrawalRequest {
  branchId: number | null;
  terminalId: number | null;
  debitAccountId: number | null;
  tellerUserId: number | null;
  amount: number | null;
  narration: string;
  transactionDate: string;
}

export interface FundTransferRequest {
  branchId: number | null;
  fromAccountId: number | null;
  toAccountId: number | null;
  transferMode: TransferMode | '';
  amount: number | null;
  remarks: string;
  transactionDate: string;
}

export interface ChequeClearingRequest {
  branchId: number | null;
  creditAccountId: number | null;
  chequeNo: string;
  draweeBank: string;
  amount: number | null;
  remarks: string;
  transactionDate: string;
}

export interface StandingInstructionRequest {
  fromAccountId: number | null;
  toAccountId: number | null;
  branchId: number | null;
  amount: number | null;
  transferMode: TransferMode | '';
  scheduleDate: string;
  frequency: string;
  remarks: string;
  status: RecordStatus | '';
}

export interface TransactionReversalRequest {
  reason: string;
}

export interface TransactionResponse {
  id: number;
  transactionRef: string;
  transactionType: string;
  channelType: string;
  branchId: number | null;
  terminalId: number | null;
  debitAccountId: number | null;
  debitAccountNumber: string | null;
  debitCustomerCode: string | null;
  debitCustomerName: string | null;
  creditAccountId: number | null;
  creditAccountNumber: string | null;
  creditCustomerCode: string | null;
  creditCustomerName: string | null;
  amount: number;
  narration: string | null;
  postedBy: string | null;
  approvedBy: string | null;
  reversalFlag: boolean;
  parentTransactionId: number | null;
  parentTransactionRef: string | null;
  transactionStatus: string | null;
  status: RecordStatus;
  transactionDate: string;
  createdAt: string;
  cashType: string | null;
  cashDirection: string | null;
  tellerUserId: number | null;
  cashRemarks: string | null;
  transferMode: string | null;
  transferRemarks: string | null;
  chequeNo: string | null;
  draweeBank: string | null;
  chequeStatus: string | null;
  chequeRemarks: string | null;
  standingInstructionId: number | null;
  standingInstructionCode: string | null;
  reversalRequestStatus: string | null;
  reversalReason: string | null;
  reversalRequestedBy: string | null;
  reversalRequestedAt: string | null;
  reversalTransactionId: number | null;
}

export interface StandingInstructionResponse {
  id: number;
  instructionCode: string;
  fromAccountId: number;
  fromAccountNumber: string | null;
  fromCustomerName: string | null;
  toAccountId: number;
  toAccountNumber: string | null;
  toCustomerName: string | null;
  branchId: number | null;
  amount: number;
  transferMode: TransferMode;
  scheduleDate: string;
  frequency: string;
  nextExecutionDate: string | null;
  instructionStatus: StandingInstructionStatus;
  remarks: string | null;
  status: RecordStatus;
  createdAt: string;
}

export interface BranchTransactionSummaryResponse {
  branchId: number | null;
  transactionCount: number;
}

export interface TransactionDashboardSummaryResponse {
  todayDepositTotal: number;
  todayWithdrawalTotal: number;
  todayTransferTotal: number;
  pendingReversals: number;
  tellerLimitUsed: number;
  tellerLimit: number;
  tellerLimitUsagePercent: number;
  suspiciousTransactionCount: number;
  topBranches: BranchTransactionSummaryResponse[];
}

export const TRANSACTION_STATUS_OPTIONS: OptionItem<string>[] = [
  { label: 'POSTED', value: 'POSTED' },
  { label: 'PENDING REVIEW', value: 'PENDING_REVIEW' },
  { label: 'REVERSED', value: 'REVERSED' },
  { label: 'FAILED', value: 'FAILED' }
];

export const TRANSFER_MODE_OPTIONS: OptionItem<TransferMode>[] = [
  { label: 'INTERNAL', value: 'INTERNAL' },
  { label: 'BEFTN', value: 'BEFTN' },
  { label: 'RTGS', value: 'RTGS' }
];

export const TRANSACTION_TYPE_OPTIONS: OptionItem<TransactionType>[] = [
  { label: 'DEPOSIT', value: 'DEPOSIT' },
  { label: 'WITHDRAWAL', value: 'WITHDRAWAL' },
  { label: 'TRANSFER', value: 'TRANSFER' },
  { label: 'CHEQUE CLEARING', value: 'CHEQUE_CLEARING' },
  { label: 'REVERSAL', value: 'REVERSAL' }
];

export const RECORD_STATUS_OPTIONS: OptionItem<RecordStatus>[] = [
  { label: 'ACTIVE', value: 'ACTIVE' },
  { label: 'PENDING', value: 'PENDING' },
  { label: 'ARCHIVED', value: 'ARCHIVED' }
];

export const STANDING_FREQUENCY_OPTIONS: OptionItem<string>[] = [
  { label: 'DAILY', value: 'DAILY' },
  { label: 'WEEKLY', value: 'WEEKLY' },
  { label: 'MONTHLY', value: 'MONTHLY' }
];

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
