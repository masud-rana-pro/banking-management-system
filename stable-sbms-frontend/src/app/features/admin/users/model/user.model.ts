export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface UserResponse {
  id: number;
  userCode?: string | null;
  username: string;
  fullName: string;
  email?: string | null;
  mobile?: string | null;
  profileImageName?: string | null;
  employeeNo?: string | null;
  designation?: string | null;
  branchId?: number | null;
  branchCode?: string | null;
  branchName?: string | null;
  userType: string;
  status: string;
  active?: boolean;
  locked?: boolean;
  emailVerified?: boolean;
  mobileVerified?: boolean;
  failedLoginCount?: number;
  lastLoginAt?: string | null;
  lockedAt?: string | null;
  passwordChangedAt?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
  createdBy?: string | null;
  updatedBy?: string | null;
  roleId?: number | null;
  roleCode?: string | null;
  roleName?: string | null;
  mappedRoles?: string[];
  historyCount?: number;
}

export interface UserBreakdown {
  label: string;
  count: number;
}

export interface UserDashboardSummary {
  totalUsers: number;
  activeUsers: number;
  lockedUsers: number;
  usersByRole: UserBreakdown[];
  usersByBranch: UserBreakdown[];
  recentLogins: UserResponse[];
}

export interface UserCreateRequest {
  username: string;
  password: string;
  fullName: string;
  email?: string | null;
  mobile?: string | null;
  profileImageName?: string | null;
  employeeNo?: string | null;
  designation?: string | null;
  branchId?: number | null;
  userType: string;
  status: string;
  roleId: number;
  active?: boolean;
  locked?: boolean;
  actionBy?: string | null;
}

export interface UserUpdateRequest {
  username?: string | null;
  fullName?: string | null;
  email?: string | null;
  mobile?: string | null;
  profileImageName?: string | null;
  employeeNo?: string | null;
  designation?: string | null;
  branchId?: number | null;
  userType?: string | null;
  status?: string | null;
  roleId?: number | null;
  active?: boolean | null;
  locked?: boolean | null;
  emailVerified?: boolean | null;
  mobileVerified?: boolean | null;
  actionBy?: string | null;
}

export interface UserRoleAssignRequest {
  roleId: number;
  actionBy?: string | null;
}

export interface UserPasswordResetRequest {
  newPassword: string;
  confirmPassword: string;
  actionBy?: string | null;
}

export interface UserLockActionRequest {
  actionBy?: string | null;
  reason?: string | null;
}

export interface UserHistoryEntry {
  eventType: string;
  status?: string | null;
  roleCode?: string | null;
  roleName?: string | null;
  ipAddress?: string | null;
  deviceInfo?: string | null;
  remarks?: string | null;
  loginTime?: string | null;
  logoutTime?: string | null;
  createdAt?: string | null;
}

export interface BranchOption {
  id: number;
  branchCode: string;
  branchName: string;
  status: string;
}
