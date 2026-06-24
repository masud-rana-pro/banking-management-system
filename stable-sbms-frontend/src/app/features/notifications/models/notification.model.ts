export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type NotificationChannelType = 'EMAIL' | 'SMS' | 'PUSH';
export type NotificationDeliveryStatus = 'PENDING' | 'SENT' | 'FAILED' | 'RETRY_QUEUED';

export interface NotificationTemplateRequest {
  templateCode?: string | null;
  templateName: string;
  channelType: NotificationChannelType | '';
  subjectText?: string | null;
  bodyText: string;
}

export interface NotificationTemplateResponse {
  id: number;
  templateCode: string;
  templateName: string;
  channelType: NotificationChannelType;
  subjectText?: string | null;
  bodyText: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface NotificationEventRequest {
  eventCode?: string | null;
  eventName: string;
  referenceModule?: string | null;
}

export interface NotificationEventResponse {
  id: number;
  eventCode: string;
  eventName: string;
  referenceModule?: string | null;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface NotificationLogResponse {
  id: number;
  eventId: number;
  eventCode: string;
  eventName: string;
  templateId: number;
  templateCode: string;
  templateName: string;
  recipientTo: string;
  channelType: NotificationChannelType;
  deliveryStatus: NotificationDeliveryStatus;
  providerResponse?: string | null;
  retryCount: number;
  sentAt?: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface NotificationChannelSummaryResponse {
  channelType: NotificationChannelType;
  sentCount: number;
  failedCount: number;
  retryQueuedCount: number;
}

export interface NotificationDashboardSummaryResponse {
  sentToday: number;
  failedToday: number;
  retryQueue: number;
  messagesSentToday: number;
  failedDeliveries: number;
  channelWiseSummary: NotificationChannelSummaryResponse[];
  recentLogs: NotificationLogResponse[];
}

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
