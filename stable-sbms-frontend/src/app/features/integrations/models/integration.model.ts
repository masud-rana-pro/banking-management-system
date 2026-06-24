export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type IntegrationProviderType = 'EMAIL' | 'SMS' | 'PAYMENT' | 'MOBILE_BANKING' | 'PUSH' | 'GENERAL';
export type IntegrationAuthType = 'NONE' | 'API_KEY' | 'BASIC' | 'BEARER' | 'USERNAME_PASSWORD';
export type IntegrationExecutionStatus = 'SUCCESS' | 'FAILED' | 'RETRY_PENDING';

export interface IntegrationProviderRequest {
  providerCode?: string | null;
  providerName: string;
  providerType: IntegrationProviderType | '';
  baseUrl: string;
  authType: IntegrationAuthType | '';
  apiKey?: string | null;
  username?: string | null;
  password?: string | null;
  timeoutSec: number | null;
}

export interface IntegrationProviderResponse {
  id: number;
  providerCode: string;
  providerName: string;
  providerType: IntegrationProviderType;
  baseUrl: string;
  authType: IntegrationAuthType;
  maskedApiKey?: string | null;
  username?: string | null;
  maskedPassword?: string | null;
  timeoutSec: number;
  status: string;
  createdAt?: string;
  updatedAt?: string;
  lastExecutionStatus?: IntegrationExecutionStatus | null;
  lastExecutedAt?: string | null;
  executionCount: number;
  failureCount: number;
}

export interface IntegrationProviderTestRequest {
  referenceModule?: string | null;
  referenceId?: number | null;
  requestPayload?: string | null;
}

export interface IntegrationExecutionLogResponse {
  id: number;
  providerId: number;
  providerCode: string;
  providerName: string;
  providerType: IntegrationProviderType;
  referenceModule?: string | null;
  referenceId?: number | null;
  requestPayload?: string | null;
  responsePayload?: string | null;
  httpStatus?: number | null;
  executionStatus: IntegrationExecutionStatus;
  executedAt?: string | null;
  retryCount: number;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface IntegrationProviderTypeSummaryResponse {
  providerType: IntegrationProviderType;
  totalProviders: number;
  activeProviders: number;
}

export interface IntegrationDashboardSummaryResponse {
  activeProviders: number;
  failedIntegrations: number;
  retryPending: number;
  lastSuccessfulSync?: string | null;
  successRate: number;
  providerTypeSummary: IntegrationProviderTypeSummaryResponse[];
  recentLogs: IntegrationExecutionLogResponse[];
}

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
