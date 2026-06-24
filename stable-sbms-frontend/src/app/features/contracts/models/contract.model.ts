export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type RecordStatus = 'ACTIVE' | 'PENDING' | 'ARCHIVED';
export type ContractType = 'ACCOUNT_OPENING' | 'FINANCING' | 'DEPOSIT_SCHEME' | 'CARD_ISSUE' | 'GENERAL';
export type ContractStatus = 'DRAFT' | 'ACTIVE' | 'LOCKED';

export interface OptionItem<T = string | number> {
  label: string;
  value: T;
}

export interface ContractTemplateRequest {
  templateCode: string;
  templateName: string;
  contractType: ContractType | '';
  versionNo: number | null;
  templateBody: string;
}

export interface ContractTemplateResponse {
  id: number;
  templateCode: string;
  templateName: string;
  contractType: ContractType;
  versionNo: number;
  templateBody: string;
  status: RecordStatus;
  createdAt?: string;
  updatedAt?: string;
  generatedContractCount: number;
}

export interface ContractGenerateRequest {
  templateId: number | null;
  customerId: number | null;
  referenceModule: string;
  referenceId: number | null;
  contractText: string;
  supportingDocumentName?: string;
  remarks: string;
  generatedBy: string;
}

export interface ContractSignRequest {
  signedBy: string;
  remarks: string;
}

export interface ContractVersionResponse {
  id: number;
  contractId: number;
  contractNo: string;
  versionNo: number;
  contractText: string;
  changeType: string;
  changedBy?: string;
  changeNote?: string;
  status: RecordStatus;
  createdAt?: string;
}

export interface ContractResponse {
  id: number;
  contractNo: string;
  contractType: ContractType;
  templateId: number;
  templateCode: string;
  templateName: string;
  templateVersionNo: number;
  customerId: number;
  customerCode: string;
  customerName: string;
  referenceModule: string;
  referenceId: number;
  contractText: string;
  supportingDocumentName?: string;
  signedByCustomer?: string;
  signedByShariah?: string;
  customerSignedAt?: string;
  shariahSignedAt?: string;
  signedDate?: string;
  contractStatus: ContractStatus;
  remarks?: string;
  status: RecordStatus;
  createdAt?: string;
  updatedAt?: string;
  versions: ContractVersionResponse[];
}

export interface ContractDashboardSummaryResponse {
  totalContracts: number;
  pendingSignatures: number;
  activeLockedContracts: number;
  contractVersions: number;
  draftContracts: number;
  recentContracts: ContractResponse[];
  recentTemplates: ContractTemplateResponse[];
}

export const CONTRACT_TYPE_OPTIONS: OptionItem<ContractType>[] = [
  { label: 'ACCOUNT OPENING', value: 'ACCOUNT_OPENING' },
  { label: 'FINANCING', value: 'FINANCING' },
  { label: 'DEPOSIT SCHEME', value: 'DEPOSIT_SCHEME' },
  { label: 'CARD ISSUE', value: 'CARD_ISSUE' },
  { label: 'GENERAL', value: 'GENERAL' }
];

export const REFERENCE_MODULE_OPTIONS: OptionItem<string>[] = [
  { label: 'ACCOUNT_OPENING', value: 'ACCOUNT_OPENING' },
  { label: 'FINANCING', value: 'FINANCING' },
  { label: 'DEPOSIT_SCHEME', value: 'DEPOSIT_SCHEME' },
  { label: 'CARD_ISSUE', value: 'CARD_ISSUE' },
  { label: 'GENERAL', value: 'GENERAL' }
];

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
