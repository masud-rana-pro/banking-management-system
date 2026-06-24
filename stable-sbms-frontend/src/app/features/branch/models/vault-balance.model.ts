export interface VaultBalanceRequest {
  branchId: number;
  balanceDate: string;
  openingBalance: number;
  remarks?: string;
}

export interface VaultCloseRequest {
  totalCashIn: number;
  totalCashOut: number;
  remarks?: string;
}

export interface VaultBalanceResponse {
  id: number;
  branchId: number;
  balanceDate: string;
  openingBalance: number;
  totalCashIn: number;
  totalCashOut: number;
  closingBalance: number;
  isClosed: boolean;
  closedBy?: number | null;
  closedAt?: string | null;
  remarks?: string;
  status: string;
  createdAt?: string;
}