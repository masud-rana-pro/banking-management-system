export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type SecuritySeverityLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type InvestigationCaseType = 'FAILED_LOGIN' | 'SUSPICIOUS_TRANSACTION' | 'AML_FLAG' | 'SANCTION_HIT' | 'AUDIT_ANOMALY' | 'OTHER';
export type InvestigationCaseStatus = 'OPEN' | 'ASSIGNED' | 'UNDER_REVIEW' | 'CLOSED';

export interface SecurityEventResponse {
  id: number;
  eventCode: string;
  eventName: string;
  userId?: number | null;
  username?: string | null;
  fullName?: string | null;
  ipAddress?: string | null;
  deviceInfo?: string | null;
  referenceModule?: string | null;
  referenceId?: number | null;
  eventTime?: string;
  severityLevel: SecuritySeverityLevel;
  remarks?: string | null;
  status: string;
  suspicious: boolean;
}

export interface AuditLogResponse {
  id: number;
  moduleName: string;
  referenceId?: number | null;
  actionName: string;
  oldValueJson?: string | null;
  newValueJson?: string | null;
  performedBy: string;
  performedAt?: string;
  status: string;
}

export interface InvestigationCaseResponse {
  id: number;
  caseNo: string;
  caseType: InvestigationCaseType;
  referenceModule: string;
  referenceId: number;
  openedBy: string;
  openedAt?: string;
  assignedTo?: number | null;
  assignedUsername?: string | null;
  assignedFullName?: string | null;
  caseStatus: InvestigationCaseStatus;
  remarks?: string | null;
  evidenceFileName?: string | null;
  status: string;
  createdAt?: string;
  auditTrail: AuditLogResponse[];
}

export interface InvestigationCaseActionRequest {
  assignedTo?: number | null;
  remarks: string;
  performedBy?: string | null;
  evidenceFileName?: string | null;
}

export interface SecurityDashboardSummaryResponse {
  failedLoginsToday: number;
  lockedUsers: number;
  suspiciousTxnCount: number;
  auditEventsToday: number;
  openInvestigationCases: number;
  amlFlagsToday: number;
  recentSecurityEvents: SecurityEventResponse[];
  recentInvestigationCases: InvestigationCaseResponse[];
  recentAuditLogs: AuditLogResponse[];
}

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
