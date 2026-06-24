export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type RecordStatus = 'ACTIVE' | 'PENDING' | 'ARCHIVED';
export type CardType = 'DEBIT_CARD' | 'ATM_CARD' | 'PREPAID_CARD' | 'VIRTUAL_CARD';
export type CardStatus = 'PENDING_ACTIVATION' | 'ACTIVE' | 'BLOCKED' | 'EXPIRED' | 'REPLACED' | 'RENEWED';
export type CardEventType =
  | 'ISSUED'
  | 'ACTIVATED'
  | 'BLOCKED'
  | 'UNBLOCKED'
  | 'REPLACED'
  | 'RENEWED'
  | 'ATM_TRANSACTION'
  | 'CDM_TRANSACTION'
  | 'ATM_USAGE_ALERT'
  | 'CDM_USAGE_ALERT';
export type CardPinEventType = 'PIN_GENERATED' | 'PIN_CHANGE' | 'PIN_RESET' | 'WRONG_PIN' | 'PIN_BLOCKED';

export interface OptionItem<T = string | number> {
  label: string;
  value: T;
}

export interface CardRequest {
  customerId: number | null;
  accountId: number | null;
  cardType: CardType | '';
  maskedCardNo: string;
  issueDate: string;
  expiryDate: string;
  cardStatus: CardStatus | '';
  blockReason: string;
}

export interface CardWorkflowActionRequest {
  blockReason: string;
  remarks: string;
  performedBy: string;
  maskedCardNo?: string;
  expiryDate?: string;
}

export interface CardPinEventRequest {
  eventType: CardPinEventType | '';
  eventDate?: string;
  performedBy: string;
}

export interface CardResponse {
  id: number;
  cardRefNo: string;
  customerId: number;
  customerCode: string;
  customerName: string;
  accountId: number;
  accountNumber: string;
  accountTypeCode: string;
  accountTypeName: string;
  branchId: number | null;
  currentBalance: number;
  cardType: CardType;
  maskedCardNo: string;
  issueDate: string;
  expiryDate: string;
  cardStatus: CardStatus;
  blockReason: string | null;
  status: RecordStatus;
  expiringSoon: boolean;
  eventCount: number;
  pinEventCount: number;
  usageAlertCount: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CardEventLogResponse {
  id: number;
  cardId: number;
  cardRefNo: string;
  eventType: CardEventType;
  eventDate: string;
  performedBy: string | null;
  remarks: string | null;
  status: RecordStatus;
  createdAt?: string;
}

export interface CardPinEventResponse {
  id: number;
  cardId: number;
  cardRefNo: string;
  eventType: CardPinEventType;
  eventDate: string;
  performedBy: string | null;
  status: RecordStatus;
  createdAt?: string;
}

export interface CardTransactionResponse {
  eventId: number;
  cardId: number;
  cardRefNo: string;
  maskedCardNo: string;
  customerId: number;
  customerCode: string;
  customerName: string;
  accountId: number;
  accountNumber: string;
  eventType: CardEventType;
  eventDate: string;
  performedBy: string | null;
  remarks: string | null;
}

export interface CardDashboardSummaryResponse {
  totalCards: number;
  activeCards: number;
  blockedCards: number;
  expiringSoon: number;
  cardTxnCount: number;
  pendingActivations: number;
  cardUsageAlertsToday: number;
  expiringCards: CardResponse[];
  pendingActivationCards: CardResponse[];
  recentUsageAlerts: CardTransactionResponse[];
}

export const CARD_TYPE_OPTIONS: OptionItem<CardType>[] = [
  { label: 'DEBIT CARD', value: 'DEBIT_CARD' },
  { label: 'ATM CARD', value: 'ATM_CARD' },
  { label: 'PREPAID CARD', value: 'PREPAID_CARD' },
  { label: 'VIRTUAL CARD', value: 'VIRTUAL_CARD' }
];

export const CARD_STATUS_OPTIONS: OptionItem<CardStatus>[] = [
  { label: 'PENDING ACTIVATION', value: 'PENDING_ACTIVATION' },
  { label: 'ACTIVE', value: 'ACTIVE' },
  { label: 'BLOCKED', value: 'BLOCKED' },
  { label: 'EXPIRED', value: 'EXPIRED' },
  { label: 'REPLACED', value: 'REPLACED' },
  { label: 'RENEWED', value: 'RENEWED' }
];

export const CARD_PIN_EVENT_OPTIONS: OptionItem<CardPinEventType>[] = [
  { label: 'PIN GENERATED', value: 'PIN_GENERATED' },
  { label: 'PIN CHANGE', value: 'PIN_CHANGE' },
  { label: 'PIN RESET', value: 'PIN_RESET' },
  { label: 'WRONG PIN', value: 'WRONG_PIN' },
  { label: 'PIN BLOCKED', value: 'PIN_BLOCKED' }
];

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
