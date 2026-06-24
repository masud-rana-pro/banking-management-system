export interface BranchCashLedgerResponse {
  id: number;
  branchId: number;
  ledgerDate: string;
  entryType: string;
  sourceType: string;
  referenceNo?: string;
  debitAmount: number;
  creditAmount: number;
  balanceAfter: number;
  remarks?: string;
  createdAt?: string;
  createdBy?: number;
}