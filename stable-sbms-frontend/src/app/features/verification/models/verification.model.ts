export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type ChannelType = 'EMAIL' | 'SMS';
export type VerificationPurpose = 'VERIFY_EMAIL' | 'VERIFY_MOBILE' | 'PASSWORD_RESET' | 'PROVIDER_TEST';
export type VerificationStatus = 'PENDING' | 'SENT' | 'VERIFIED' | 'EXPIRED' | 'FAILED';
export type TokenType = 'OTP' | 'PASSWORD_RESET';

export interface VerificationSendOtpRequest {
  userId?: number | null;
  customerId?: number | null;
  referenceModule?: string;
  referenceId?: number | null;
  contactValue: string;
  remarks: string;
}

export interface VerificationOtpVerifyRequest {
  requestId: number | null;
  otpCode: string;
  ipAddress?: string;
  deviceInfo?: string;
  createdBy?: string;
}

export interface ForgotPasswordRequest {
  identifier: string;
  channelType: ChannelType;
}

export interface ResetPasswordRequest {
  requestId: number | null;
  otpCode: string;
  newPassword: string;
  confirmPassword: string;
}

export interface ProviderTestRequest {
  channelType: ChannelType;
  purpose: VerificationPurpose;
  contactValue: string;
  referenceModule?: string;
  referenceId?: number | null;
  remarks: string;
}

export interface VerificationChannelResponse {
  id: number;
  channelCode: string;
  channelName: string;
  providerName?: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface VerificationTemplateResponse {
  id: number;
  purpose: VerificationPurpose;
  channelType: ChannelType;
  templateCode: string;
  templateName: string;
  subjectLine?: string;
  templateBody: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface VerificationAttemptResponse {
  id: number;
  attemptType: string;
  attemptValueMasked?: string;
  attemptStatus: string;
  remarks?: string;
  ipAddress?: string;
  deviceInfo?: string;
  createdBy?: string;
  createdAt?: string;
}

export interface VerificationLogResponse {
  id: number;
  purpose: VerificationPurpose;
  channelType: ChannelType;
  tokenType: TokenType;
  contactValue: string;
  contactValueMasked: string;
  userId?: number;
  username?: string;
  customerId?: number;
  customerCode?: string;
  customerName?: string;
  referenceModule?: string;
  referenceId?: number;
  attemptCount: number;
  maxAttemptCount: number;
  requestStatus: VerificationStatus;
  providerResponse?: string;
  sentAt?: string;
  expiresAt?: string;
  usedAt?: string;
  contactVerified: boolean;
  lastDispatchStatus: string;
  attempts: VerificationAttemptResponse[];
}

export interface VerificationDashboardSummaryResponse {
  pendingRequests: number;
  verifiedRequests: number;
  failedRequests: number;
  passwordResetRequests: number;
  providerDispatchCount: number;
  unverifiedEmailCount: number;
  unverifiedMobileCount: number;
  recentRequests: VerificationLogResponse[];
  recentAttempts: VerificationAttemptResponse[];
  recentContactStatuses: VerificationContactStatusResponse[];
}

export interface VerificationContactStatusResponse {
  id: number;
  referenceModule: string;
  referenceId: number;
  contactType: string;
  contactValue: string;
  isPrimary: boolean;
  isVerified: boolean;
  verifiedAt?: string | null;
  verifiedBy?: string | null;
  verificationMethod?: string | null;
  lastVerificationRequestId?: number | null;
  status: string;
  updatedAt?: string | null;
}

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
