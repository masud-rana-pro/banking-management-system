export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface LookupTypeResponse {
  id: number;
  typeCode: string;
  typeName: string;
  description?: string | null;
  status: string;
  createdAt?: string;
  updatedAt?: string;
  activeValueCount: number;
  totalValueCount: number;
}

export interface LookupValueResponse {
  id: number;
  lookupTypeId: number;
  typeCode: string;
  typeName: string;
  valueCode: string;
  valueLabel: string;
  valueBnLabel?: string | null;
  sortOrder?: number | null;
  extraData?: string | null;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface LookupDashboardSummaryResponse {
  lookupTypeCount: number;
  lookupValueCount: number;
  activeValueCount: number;
  recentlyChangedCount: number;
  recentTypes: LookupTypeResponse[];
  recentValues: LookupValueResponse[];
}

export interface LookupTypeRequest {
  typeCode: string;
  typeName: string;
  description?: string | null;
  status: string;
}

export interface LookupValueRequest {
  lookupTypeId: number | null;
  valueCode: string;
  valueLabel: string;
  valueBnLabel?: string | null;
  sortOrder?: number | null;
  extraData?: string | null;
  status: string;
}

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
