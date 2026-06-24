export interface BranchAssignmentResponse {
  id: number;
  branchId: number;
  userId: number;
  assignmentRole: string;
  fromDate: string;
  toDate?: string;
  isPrimary: boolean;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface BranchAssignmentRequest {
  branchId: number;
  userId: number;
  assignmentRole: string;
  fromDate: string;
  toDate?: string | null;
  isPrimary: boolean;
  status: string;
}