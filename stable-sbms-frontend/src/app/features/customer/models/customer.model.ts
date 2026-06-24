export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type CustomerType = 'INDIVIDUAL' | 'JOINT' | 'CORPORATE' | 'SME';
export type CustomerStatus = 'DRAFT' | 'PENDING_KYC' | 'ACTIVE' | 'BLOCKED' | 'REJECTED' | 'CLOSED';
export type RecordStatus = 'ACTIVE' | 'PENDING' | 'ARCHIVED';
export type Gender = 'MALE' | 'FEMALE' | 'OTHER';
export type MaritalStatus = 'SINGLE' | 'MARRIED' | 'DIVORCED' | 'WIDOWED';
export type AddressType = 'PRESENT' | 'PERMANENT' | 'OFFICE' | 'REGISTERED';
export type DocumentType = 'NID' | 'PASSPORT' | 'BIRTH_CERTIFICATE' | 'TRADE_LICENSE' | 'TIN' | 'DRIVING_LICENSE';

export interface CustomerRequest {
  customerType: CustomerType | '';
  fullName: string;
  fatherName: string;
  motherName: string;
  spouseName: string;
  dateOfBirth: string;
  gender: Gender | '';
  maritalStatus: MaritalStatus | '';
  nationality: string;
  mobile: string;
  email: string;
  profileImageName: string;
  occupation: string;
  monthlyIncome: number | null;
  sourceOfFunds: string;
  branchId: number | null;
  customerStatus: CustomerStatus | '';
  status: RecordStatus | '';
}

export interface CustomerResponse extends CustomerRequest {
  id: number;
  customerCode: string;
  mobileVerified?: boolean;
  emailVerified?: boolean;
  addressCount?: number;
  identityCount?: number;
  verifiedIdentityCount?: number;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
}

export interface CustomerDropdownResponse {
  id: number;
  customerCode: string;
  fullName: string;
  mobile: string;
  displayName: string;
}

export interface CustomerTimelineResponse {
  title: string;
  description: string;
  type: string;
  activityTime?: string;
}

export interface CustomerDashboardSummaryResponse {
  totalCustomers: number;
  activeCustomers: number;
  pendingKycCustomers: number;
  blockedCustomers: number;
  newCustomersThisMonth: number;
  incompleteProfiles: number;
}

export interface CustomerAddressRequest {
  customerId: number | null;
  addressType: AddressType | '';
  addressLine1: string;
  addressLine2: string;
  countryId: number | null;
  divisionId: number | null;
  districtId: number | null;
  upazilaId: number | null;
  postalCode: string;
  primaryAddress: boolean;
  status: RecordStatus | '';
}

export interface CustomerAddressResponse extends CustomerAddressRequest {
  id: number;
  customerCode: string;
  customerName: string;
  createdAt?: string;
}

export interface CustomerIdentityRequest {
  customerId: number | null;
  documentType: DocumentType | '';
  documentNo: string;
  issueDate: string;
  expiryDate: string;
  issueCountry: string;
  imageFileName: string;
  verifiedFlag: boolean;
  status: RecordStatus | '';
}

export interface CustomerIdentityResponse extends CustomerIdentityRequest {
  id: number;
  customerCode: string;
  customerName: string;
  createdAt?: string;
}

export interface OptionItem<T = string | number> {
  label: string;
  value: T;
}

export interface LocationOption {
  id: number;
  name: string;
  parentId?: number;
}

export const CUSTOMER_TYPE_OPTIONS: OptionItem<CustomerType>[] = [
  { label: 'INDIVIDUAL', value: 'INDIVIDUAL' },
  { label: 'JOINT', value: 'JOINT' },
  { label: 'CORPORATE', value: 'CORPORATE' },
  { label: 'SME', value: 'SME' }
];

export const CUSTOMER_STATUS_OPTIONS: OptionItem<CustomerStatus>[] = [
  { label: 'DRAFT', value: 'DRAFT' },
  { label: 'PENDING KYC', value: 'PENDING_KYC' },
  { label: 'ACTIVE', value: 'ACTIVE' },
  { label: 'BLOCKED', value: 'BLOCKED' },
  { label: 'REJECTED', value: 'REJECTED' },
  { label: 'CLOSED', value: 'CLOSED' }
];

export const RECORD_STATUS_OPTIONS: OptionItem<RecordStatus>[] = [
  { label: 'ACTIVE', value: 'ACTIVE' },
  { label: 'PENDING', value: 'PENDING' },
  { label: 'ARCHIVED', value: 'ARCHIVED' }
];

export const GENDER_OPTIONS: OptionItem<Gender>[] = [
  { label: 'MALE', value: 'MALE' },
  { label: 'FEMALE', value: 'FEMALE' },
  { label: 'OTHER', value: 'OTHER' }
];

export const MARITAL_STATUS_OPTIONS: OptionItem<MaritalStatus>[] = [
  { label: 'SINGLE', value: 'SINGLE' },
  { label: 'MARRIED', value: 'MARRIED' },
  { label: 'DIVORCED', value: 'DIVORCED' },
  { label: 'WIDOWED', value: 'WIDOWED' }
];

export const ADDRESS_TYPE_OPTIONS: OptionItem<AddressType>[] = [
  { label: 'PRESENT', value: 'PRESENT' },
  { label: 'PERMANENT', value: 'PERMANENT' },
  { label: 'OFFICE', value: 'OFFICE' },
  { label: 'REGISTERED', value: 'REGISTERED' }
];

export const DOCUMENT_TYPE_OPTIONS: OptionItem<DocumentType>[] = [
  { label: 'NID', value: 'NID' },
  { label: 'PASSPORT', value: 'PASSPORT' },
  { label: 'BIRTH CERTIFICATE', value: 'BIRTH_CERTIFICATE' },
  { label: 'TRADE LICENSE', value: 'TRADE_LICENSE' },
  { label: 'TIN', value: 'TIN' },
  { label: 'DRIVING LICENSE', value: 'DRIVING_LICENSE' }
];

export const COUNTRY_OPTIONS: LocationOption[] = [
  { id: 1, name: 'Bangladesh' },
  { id: 2, name: 'Saudi Arabia' },
  { id: 3, name: 'United Arab Emirates' }
];

export const DIVISION_OPTIONS: LocationOption[] = [
  { id: 101, name: 'Dhaka', parentId: 1 },
  { id: 102, name: 'Chattogram', parentId: 1 },
  { id: 103, name: 'Khulna', parentId: 1 }
];

export const DISTRICT_OPTIONS: LocationOption[] = [
  { id: 1001, name: 'Dhaka District', parentId: 101 },
  { id: 1002, name: 'Gazipur', parentId: 101 },
  { id: 1003, name: 'Chattogram District', parentId: 102 },
  { id: 1004, name: 'Khulna District', parentId: 103 }
];

export const UPAZILA_OPTIONS: LocationOption[] = [
  { id: 10001, name: 'Dhanmondi', parentId: 1001 },
  { id: 10002, name: 'Uttara', parentId: 1001 },
  { id: 10003, name: 'Tongi', parentId: 1002 },
  { id: 10004, name: 'Pahartali', parentId: 1003 },
  { id: 10005, name: 'Sonadanga', parentId: 1004 }
];

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
