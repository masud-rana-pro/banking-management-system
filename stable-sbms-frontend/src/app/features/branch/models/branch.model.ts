export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export interface BranchRequest {
  branchCode?: string;
  branchName: string;
  branchShortName?: string;
  branchType: string;
  routingNo: string;
  swiftCode?: string;
  email?: string;
  mobile?: string;
  phone?: string;
  addressLine1: string;
  addressLine2?: string;
  countryId?: number | null;
  divisionId?: number | null;
  districtId?: number | null;
  upazilaId?: number | null;
  postalCode?: string;
  managerUserId?: number | null;
  openedDate?: string;
  status: string;
}

export interface BranchResponse extends BranchRequest {
  id: number;
  branchCode: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface BranchDashboardSummaryResponse {
  totalBranches: number;
  activeBranches: number;
  branchCashPosition: number;
  pendingAssignments: number;
  tellerLimitAlerts: number;
  todayVaultOpened: number;
  todayVaultClosed: number;
  todayVaultPendingClose: number;
}
