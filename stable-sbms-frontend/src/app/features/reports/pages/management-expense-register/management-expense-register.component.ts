import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { BranchResponse } from 'src/app/features/branch/models/branch.model';
import { BranchApiService } from 'src/app/features/branch/services/branch-api.service';
import { ManagementExpenseEntryRequest, ManagementExpenseEntryResponse } from '../../models/report.model';
import { ReportService } from '../../services/report.service';

@Component({
  selector: 'app-management-expense-register',
  templateUrl: './management-expense-register.component.html',
  styleUrls: ['./management-expense-register.component.scss']
})
export class ManagementExpenseRegisterComponent implements OnInit {

  branches: BranchResponse[] = [];
  items: ManagementExpenseEntryResponse[] = [];
  loading = false;
  saving = false;

  expenseCategories = [
    'SALARY_ALLOWANCE',
    'RENT_UTILITY',
    'IT_SYSTEMS',
    'COMPLIANCE_AUDIT',
    'OFFICE_ADMIN',
    'OTHER_OPERATING'
  ];

  createForm = {
    expenseDate: this.today(),
    branchId: null as number | null,
    expenseCategory: 'OFFICE_ADMIN',
    expenseCode: '',
    amount: null as number | null,
    referenceNo: '',
    remarks: ''
  };

  filters = {
    dateFrom: this.daysAgo(30),
    dateTo: this.today(),
    branchId: null as number | null,
    expenseCategory: '',
    keyword: ''
  };

  constructor(
    private reportService: ReportService,
    private branchApi: BranchApiService,
    private accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadBranches();
    this.loadEntries();
  }

  loadBranches(): void {
    this.branchApi.dropdown().subscribe({
      next: data => this.branches = data || [],
      error: () => this.branches = []
    });
  }

  loadEntries(): void {
    this.loading = true;
    this.reportService.getManagementExpenseEntries(this.filters).subscribe({
      next: data => {
        this.items = data || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.items = [];
        this.loading = false;
        Swal.fire('Error', 'Failed to load management expense entries.', 'error');
      }
    });
  }

  save(): void {
    if (!this.createForm.amount || this.createForm.amount <= 0) {
      Swal.fire('Validation', 'Expense amount must be greater than zero.', 'warning');
      return;
    }

    const request: ManagementExpenseEntryRequest = {
      expenseDate: this.createForm.expenseDate,
      branchId: this.createForm.branchId,
      expenseCategory: this.createForm.expenseCategory,
      expenseCode: this.nullIfBlank(this.createForm.expenseCode),
      amount: this.createForm.amount,
      referenceNo: this.nullIfBlank(this.createForm.referenceNo),
      remarks: this.nullIfBlank(this.createForm.remarks)
    };

    this.saving = true;
    this.reportService.createManagementExpenseEntry(request, this.accessControl.session?.username || 'SYSTEM').subscribe({
      next: () => {
        this.saving = false;
        Swal.fire('Saved', 'Management expense entry recorded successfully.', 'success');
        this.createForm = {
          expenseDate: this.today(),
          branchId: null,
          expenseCategory: this.createForm.expenseCategory,
          expenseCode: '',
          amount: null,
          referenceNo: '',
          remarks: ''
        };
        this.loadEntries();
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save management expense entry.', 'error');
      }
    });
  }

  resetFilters(): void {
    this.filters = {
      dateFrom: this.daysAgo(30),
      dateTo: this.today(),
      branchId: null,
      expenseCategory: '',
      keyword: ''
    };
    this.loadEntries();
  }

  formatAmount(value?: number | null): string {
    return Number(value || 0).toLocaleString('en-BD', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  formatCategory(value?: string | null): string {
    return String(value || '').replace(/_/g, ' ');
  }

  private today(): string {
    return new Date().toISOString().slice(0, 10);
  }

  private daysAgo(days: number): string {
    const value = new Date();
    value.setDate(value.getDate() - days);
    return value.toISOString().slice(0, 10);
  }

  private nullIfBlank(value?: string | null): string | null {
    return value && value.trim() ? value.trim() : null;
  }
}
