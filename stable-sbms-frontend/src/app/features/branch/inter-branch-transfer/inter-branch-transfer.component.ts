import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2';

import { BranchApiService } from '../services/branch-api.service';
import { BranchResponse } from '../models/branch.model';

interface TransferPreview {
  referenceNo: string;
  sourceBranchName: string;
  targetBranchName: string;
  transferDate: string;
  amount: number;
  remarks: string;
  approvalNote: string;
}

@Component({
  selector: 'app-inter-branch-transfer',
  templateUrl: './inter-branch-transfer.component.html',
  styleUrls: ['./inter-branch-transfer.component.scss']
})
export class InterBranchTransferComponent implements OnInit {

  branches: BranchResponse[] = [];
  preview: TransferPreview | null = null;

  form = {
    sourceBranchId: '',
    targetBranchId: '',
    transferDate: new Date().toISOString().slice(0, 10),
    amount: null as number | null,
    remarks: '',
    approvalNote: 'Branch manager approval trail must be recorded before execution.'
  };

  constructor(private branchApi: BranchApiService) {}

  ngOnInit(): void {
    this.branchApi.getAll('', 'ACTIVE').subscribe({
      next: data => this.branches = data || [],
      error: () => this.branches = []
    });
  }

  prepare(): void {
    if (!this.validate()) {
      return;
    }

    this.preview = {
      referenceNo: `IBT-${Date.now().toString().slice(-8)}`,
      sourceBranchName: this.getBranchLabel(Number(this.form.sourceBranchId)),
      targetBranchName: this.getBranchLabel(Number(this.form.targetBranchId)),
      transferDate: this.form.transferDate,
      amount: Number(this.form.amount || 0),
      remarks: this.form.remarks,
      approvalNote: this.form.approvalNote
    };

    Swal.fire('Prepared', 'Inter-branch transfer review memo prepared successfully.', 'success');
  }

  reset(): void {
    this.preview = null;
    this.form = {
      sourceBranchId: '',
      targetBranchId: '',
      transferDate: new Date().toISOString().slice(0, 10),
      amount: null,
      remarks: '',
      approvalNote: 'Branch manager approval trail must be recorded before execution.'
    };
  }

  getBranchLabel(branchId: number): string {
    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `Branch #${branchId}`;
  }

  private validate(): boolean {
    if (!this.form.sourceBranchId || !this.form.targetBranchId) {
      Swal.fire('Validation', 'Source and target branch are required.', 'warning');
      return false;
    }
    if (this.form.sourceBranchId === this.form.targetBranchId) {
      Swal.fire('Validation', 'Source and target branch cannot be the same.', 'warning');
      return false;
    }
    if (!this.form.transferDate) {
      Swal.fire('Validation', 'Transfer date is required.', 'warning');
      return false;
    }
    if (!this.form.amount || this.form.amount <= 0) {
      Swal.fire('Validation', 'Amount must be greater than zero.', 'warning');
      return false;
    }
    return true;
  }
}
