export interface TellerLimitResponse {
  id: number;
  branchId: number;
  userId: number;
  limitDate: string;
  dailyDepositLimit: number;
  dailyWithdrawLimit: number;
  singleTxnLimit: number;
  approvedBy?: number | null;
  approvedAt?: string | null;
  status: string;
  createdAt?: string;
}

export interface TellerLimitRequest {
  branchId: number;
  userId: number;
  limitDate: string;
  dailyDepositLimit: number;
  dailyWithdrawLimit: number;
  singleTxnLimit: number;
  status: string;
}