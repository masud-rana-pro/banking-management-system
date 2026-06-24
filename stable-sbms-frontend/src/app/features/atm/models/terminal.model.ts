export type TerminalType = 'ATM' | 'CDM' | 'ATM_CDM';

export type TerminalStatus =
  | 'ACTIVE'
  | 'INACTIVE'
  | 'MAINTENANCE'
  | 'OUT_OF_SERVICE'
  | 'ARCHIVED';

export interface TerminalRequest {
  terminalCode: string;
  terminalName: string;
  terminalType: TerminalType | '';
  branchId: number | null;
  locationNote: string;
  ipAddress: string;
  serialNo: string;
  vendorName: string;
  installDate: string;
  status: TerminalStatus;
}

export interface TerminalResponse {
  id: number;
  terminalCode: string;
  terminalName: string;
  terminalType: TerminalType;
  branchId: number;
  locationNote?: string;
  ipAddress?: string;
  serialNo?: string;
  vendorName?: string;
  installDate?: string;
  status: TerminalStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface TerminalDropdownResponse {
  id: number;
  terminalCode: string;
  terminalName: string;
}

export type CashBinStatus =
  | 'ACTIVE'
  | 'LOW_CASH'
  | 'FULL'
  | 'INACTIVE'
  | 'ARCHIVED';

export interface CashBinRequest {
  terminalId: number | null;
  binNo: string;
  denomination: number | null;
  maxCapacity: number | null;
  currentCount: number | null;
  status: CashBinStatus;
}

export interface CashBinResponse {
  id: number;
  terminalId: number;
  terminalCode: string;
  terminalName: string;
  binNo: string;
  denomination: number;
  maxCapacity: number;
  currentCount: number;
  currentAmount: number;
  status: CashBinStatus;
  createdAt?: string;
}

export type ReplenishmentStatus = 'COMPLETED' | 'CANCELLED';

export interface ReplenishmentRequest {
  terminalId: number | null;
  replenishmentDate: string;
  binNo: string;
  quantityAdded: number | null;
  performedBy: number | null;
  remarks: string;
  status: ReplenishmentStatus;
}

export interface ReplenishmentResponse {
  id: number;
  terminalId: number;
  terminalCode: string;
  terminalName: string;
  replenishmentDate: string;
  binNo: string;
  denomination: number;
  quantityAdded: number;
  amountAdded: number;
  performedBy: number;
  remarks?: string;
  status: ReplenishmentStatus;
  createdAt?: string;
}

export type ReconciliationStatus = 'MATCHED' | 'VARIANCE_FOUND' | 'APPROVED';

export interface ReconciliationRequest {
  terminalId: number | null;
  reconDate: string;
  systemAmount: number | null;
  physicalAmount: number | null;
  approvedBy: number | null;
  remarks: string;
  status?: ReconciliationStatus;
}

export interface ReconciliationResponse {
  id: number;
  terminalId: number;
  terminalCode: string;
  terminalName: string;
  reconDate: string;
  systemAmount: number;
  physicalAmount: number;
  varianceAmount: number;
  approvedBy?: number | null;
  approvedAt?: string;
  remarks?: string;
  status: ReconciliationStatus;
  createdAt?: string;
}

export interface AtmDashboardSummaryResponse {
  totalTerminals: number;
  activeTerminals: number;
  lowCashAlerts: number;
  unreconciledTerminals: number;
  downtimeTerminals: number;
  todayVolumeCount: number;
  todayVolumeAmount: number;
}

export interface DeviceJournalResponse {
  terminalId: number;
  terminalCode: string;
  terminalName: string;
  eventType: string;
  referenceNo: string;
  amount: number;
  status: string;
  remarks?: string;
  eventDate?: string;
}

export interface UserSummaryResponse {
  id: number;
  username: string;
  fullName: string;
  email?: string;
  mobile?: string;
  roleId?: number;
  roleCode?: string;
  roleName?: string;
  status?: string;
}
