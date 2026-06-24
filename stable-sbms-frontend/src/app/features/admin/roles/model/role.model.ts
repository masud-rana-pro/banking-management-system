export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface RolePermissionResponse {
  moduleName: string;
  actionName: string;
  permissionCode: string;
  displayName: string;
  allowed: boolean;
}

export interface RoleResponse {
  id: number;
  code: string;
  name: string;
  description?: string | null;
  status: string;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string | null;
  updatedBy?: string | null;
  assignedUserCount?: number;
  permissionCount?: number;
  permissions?: RolePermissionResponse[];
}

export interface RoleDashboardSummary {
  totalRoles: number;
  activeRoles: number;
  inactiveRoles: number;
  permissionHeavyRoles: number;
  recentRoles: RoleResponse[];
}

export interface RoleCreateRequest {
  code: string;
  name: string;
  description?: string | null;
  status: string;
  actionBy?: string | null;
}

export interface RolePermissionAssignRequest {
  permissionCodes: string[];
  createdBy?: string | null;
}

