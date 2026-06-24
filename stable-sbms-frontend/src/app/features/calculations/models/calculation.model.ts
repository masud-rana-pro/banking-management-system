export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type CalculationSourceModule = 'GENERAL' | 'FINANCING' | 'DEPOSIT_SCHEME' | 'PROFIT';
export type CalculationFrequency = 'MONTHLY' | 'QUARTERLY' | 'HALF_YEARLY' | 'YEARLY';

export interface CalculationSimulateRequest {
  sourceModule: CalculationSourceModule | string;
  productType: string;
  principalAmount: number | null;
  ratePercent: number | null;
  tenureMonths: number | null;
  frequency: CalculationFrequency | '';
  startDate: string;
  remarks: string;
}

export interface CalculationScheduleItemResponse {
  periodNo: number;
  dueDate: string;
  principalComponent: number;
  profitComponent: number;
  totalAmount: number;
  outstandingBalance: number;
  note: string;
}

export interface CalculationSimulationResponse {
  sourceModule: string;
  productType: string;
  formulaName: string;
  principalAmount: number;
  ratePercent: number;
  tenureMonths: number;
  frequency: string;
  startDate: string;
  totalPrincipal: number;
  totalProfit: number;
  totalPayable: number;
  periodicAmount: number;
  residualAmount: number;
  assumptions: string[];
  schedule: CalculationScheduleItemResponse[];
}

export interface CalculationMetricResponse {
  productType: string;
  usageCount: number;
}

export interface CalculationRecentItemResponse {
  sourceModule: string;
  referenceNo: string;
  productType: string;
  customerOrAccount: string;
  amount: number;
  status: string;
  eventDate: string;
  routeHint: string;
}

export interface CalculationDashboardSummaryResponse {
  recentCalculations: number;
  failedCalculations: number;
  financingSimulationCount: number;
  depositSimulationCount: number;
  profitSimulationCount: number;
  productWiseSimulationCounts: CalculationMetricResponse[];
  recentItems: CalculationRecentItemResponse[];
}

export interface OptionItem<T = string | number> {
  label: string;
  value: T;
}

export const CALCULATION_SOURCE_OPTIONS: OptionItem<CalculationSourceModule>[] = [
  { label: 'GENERAL', value: 'GENERAL' },
  { label: 'FINANCING', value: 'FINANCING' },
  { label: 'DEPOSIT SCHEME', value: 'DEPOSIT_SCHEME' },
  { label: 'PROFIT', value: 'PROFIT' }
];

export const CALCULATION_FREQUENCY_OPTIONS: OptionItem<CalculationFrequency>[] = [
  { label: 'MONTHLY', value: 'MONTHLY' },
  { label: 'QUARTERLY', value: 'QUARTERLY' },
  { label: 'HALF YEARLY', value: 'HALF_YEARLY' },
  { label: 'YEARLY', value: 'YEARLY' }
];

export const CALCULATION_PRODUCT_OPTIONS: OptionItem<string>[] = [
  { label: 'MURABAHA', value: 'MURABAHA' },
  { label: 'IJARAH', value: 'IJARAH' },
  { label: 'MUSHARAKA', value: 'MUSHARAKA' },
  { label: 'MUDARABA', value: 'MUDARABA' },
  { label: 'SALAM', value: 'SALAM' },
  { label: 'ISTISNA', value: 'ISTISNA' },
  { label: 'MONTHLY SAVINGS', value: 'MONTHLY_SAVINGS' },
  { label: 'HAJJ SAVINGS', value: 'HAJJ_SAVINGS' },
  { label: 'EDUCATION SAVINGS', value: 'EDUCATION_SAVINGS' },
  { label: 'MARRIAGE SAVINGS', value: 'MARRIAGE_SAVINGS' },
  { label: 'PENSION SAVINGS', value: 'PENSION_SAVINGS' },
  { label: 'GENERAL SAVINGS', value: 'GENERAL_SAVINGS' },
  { label: 'PROFIT POSTING', value: 'PROFIT_POSTING' }
];

export function formatEnumLabel(value?: string | null): string {
  return String(value || '').replace(/_/g, ' ');
}
