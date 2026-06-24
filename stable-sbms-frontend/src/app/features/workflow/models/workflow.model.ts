export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export interface WorkflowCommentResponse {
  id: number;
  moduleName: string;
  referenceId: number;
  commentText: string;
  commentBy: string;
  commentAt?: string;
  status: string;
}

export interface WorkflowHistoryResponse {
  id: number;
  moduleName: string;
  referenceId: number;
  actionName: string;
  fromStatus?: string | null;
  toStatus?: string | null;
  actionBy: string;
  actionAt?: string;
  remarks?: string | null;
  status: string;
  comments: WorkflowCommentResponse[];
}

export interface WorkflowDashboardSummaryResponse {
  pendingApprovals: number;
  myPendingTasks: number;
  recentCompletedTasks: number;
  workflowBottlenecks: number;
  recentHistory: WorkflowHistoryResponse[];
  pendingQueue: WorkflowHistoryResponse[];
}

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}

export function resolveWorkflowSourceRoute(item: Pick<WorkflowHistoryResponse, 'moduleName' | 'referenceId'>): string {
  const moduleName = String(item.moduleName || '').toUpperCase();

  if (moduleName === 'KYC') return `/kyc/${item.referenceId}`;
  if (moduleName === 'CUSTOMER') return `/customers/${item.referenceId}`;
  if (moduleName === 'ACCOUNT') return `/accounts/${item.referenceId}`;
  if (moduleName === 'FINANCING') return `/financing/applications/${item.referenceId}`;
  if (moduleName === 'SHARIAH') return `/shariah/cases/${item.referenceId}`;
  if (moduleName === 'SECURITY_INVESTIGATION') return `/security/investigation-cases/${item.referenceId}`;
  if (moduleName === 'NOTIFICATION') return '/notifications/logs';
  if (moduleName === 'REPORTS') return '/reports/export-history';
  if (moduleName === 'CONTRACT') return `/contracts/${item.referenceId}`;
  if (moduleName === 'ATM') return `/atm/terminals/${item.referenceId}`;
  return '/workflow/dashboard';
}
