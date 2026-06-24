import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { BranchApiService } from '../services/branch-api.service';
import { BranchAssignmentApiService } from '../services/branch-assignment-api.service';
import { BranchResponse } from '../models/branch.model';
import { BranchAssignmentRequest, BranchAssignmentResponse } from '../models/branch-assignment.model';

@Component({
  selector: 'app-branch-assignment',
  templateUrl: './branch-assignment.component.html',
  styleUrls: ['./branch-assignment.component.scss']
})
export class BranchAssignmentComponent implements OnInit {

  branches: BranchResponse[] = [];
  filteredBranchOptions: BranchResponse[] = [];

  allAssignments: BranchAssignmentResponse[] = [];
  assignments: BranchAssignmentResponse[] = [];
  filteredAssignments: BranchAssignmentResponse[] = [];

  branchSearch = '';

  loading = false;
  saving = false;
  userImageMap: Record<string, string> = {};
  userDisplayMap: Record<number, string> = {};

  page = 1;
  pageSize = 10;
  total = 0;

  editingId: number | null = null;

  filters: { branchSearch: string; status: string } = {
    branchSearch: '',
    status: ''
  };

  form: BranchAssignmentRequest = {
    branchId: 0,
    userId: 0,
    assignmentRole: '',
    fromDate: '',
    toDate: null,
    isPrimary: false,
    status: 'ACTIVE'
  };

  roleOptions = [
    { value: '', label: 'Select Role' },
    { value: 'BRANCH_MANAGER', label: 'Branch Manager' },
    { value: 'TELLER', label: 'Teller' },
    { value: 'OPERATIONS_OFFICER', label: 'Operations Officer' },
    { value: 'CASH_OFFICER', label: 'Cash Officer' },
    { value: 'AUDITOR', label: 'Auditor' },
    { value: 'SUPERVISOR', label: 'Supervisor' }
  ];

  statusOptions = [
    { value: '', label: 'All Status' },
    { value: 'ACTIVE', label: 'ACTIVE' },
    { value: 'INACTIVE', label: 'INACTIVE' }
  ];

  constructor(
    private branchApi: BranchApiService,
    private assignmentApi: BranchAssignmentApiService,
    private userApi: UserApiService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadBranches();
    this.loadAssignments();
  }

  private safe(value: any): string {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  loadBranches(): void {
    this.branchApi.getAll('', 'ACTIVE').subscribe({
      next: data => {
        this.branches = data || [];
        this.filteredBranchOptions = [...this.branches];
      },
      error: () => {
        this.branches = [];
        this.filteredBranchOptions = [];
      }
    });
  }

  loadAssignments(): void {
    this.loading = true;

    this.assignmentApi.getAll(null, '').subscribe({
      next: data => {
        this.allAssignments = data || [];
        this.applyFiltersAndPaging(true);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.allAssignments = [];
        this.applyFiltersAndPaging(true);
        this.loading = false;
        Swal.fire('Error', 'Failed to load branch assignments', 'error');
      }
    });
  }

  onBranchSearchChange(): void {
    const keyword = (this.branchSearch || '').trim().toLowerCase();

    this.filteredBranchOptions = !keyword
      ? [...this.branches]
      : this.branches.filter(b =>
          (b.branchCode || '').toLowerCase().includes(keyword) ||
          (b.branchName || '').toLowerCase().includes(keyword) ||
          (b.routingNo || '').toLowerCase().includes(keyword)
        );
  }

  onSearch(): void {
    this.applyFiltersAndPaging(true);
  }

  onReset(): void {
    this.filters = { branchSearch: '', status: '' };
    this.applyFiltersAndPaging(true);
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFiltersAndPaging(false);
  }

  saveAssignment(): void {
    if (!this.validateForm()) return;

    this.saving = true;

    const request: BranchAssignmentRequest = {
      ...this.form,
      branchId: Number(this.form.branchId),
      userId: Number(this.form.userId),
      toDate: this.form.toDate || null
    };

    const apiCall = this.editingId
      ? this.assignmentApi.update(this.editingId, request)
      : this.assignmentApi.create(request);

    apiCall.subscribe({
      next: () => {
        this.saving = false;
        Swal.fire({
          icon: 'success',
          title: this.editingId ? 'Updated' : 'Assigned',
          text: this.editingId ? 'Assignment updated successfully.' : 'User assigned to branch successfully.',
          timer: 1400,
          showConfirmButton: false
        });

        this.resetForm();
        this.loadAssignments();
      },
      error: err => {
        this.saving = false;
        Swal.fire({
          icon: 'error',
          title: 'Save failed',
          text: err?.error?.message || 'Please check assignment rules and try again.'
        });
      }
    });
  }

  editAssignment(item: BranchAssignmentResponse): void {
    this.editingId = item.id;

    this.form = {
      branchId: item.branchId,
      userId: item.userId,
      assignmentRole: item.assignmentRole,
      fromDate: item.fromDate,
      toDate: item.toDate || null,
      isPrimary: item.isPrimary,
      status: item.status
    };

    const selectedBranch = this.branches.find(b => b.id === item.branchId);
    this.branchSearch = selectedBranch ? `${selectedBranch.branchCode} - ${selectedBranch.branchName}` : '';
    this.onBranchSearchChange();
  }

  deactivate(item: BranchAssignmentResponse): void {
    Swal.fire({
      icon: 'warning',
      title: 'Deactivate assignment?',
      text: `User ${item.userId} will be inactive for this branch role.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, deactivate',
      cancelButtonText: 'Cancel'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.assignmentApi.deactivate(item.id).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Deactivated',
            text: 'Assignment deactivated successfully.',
            timer: 1400,
            showConfirmButton: false
          });
          this.loadAssignments();
        },
        error: () => Swal.fire('Error', 'Deactivate failed.', 'error')
      });
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.branchSearch = '';
    this.filteredBranchOptions = [...this.branches];

    this.form = {
      branchId: 0,
      userId: 0,
      assignmentRole: '',
      fromDate: '',
      toDate: null,
      isPrimary: false,
      status: 'ACTIVE'
    };
  }

  getBranchName(branchId: number): string {
    return this.branches.find(b => b.id === branchId)?.branchName || `Branch #${branchId}`;
  }

  getBranchCode(branchId: number): string {
    return this.branches.find(b => b.id === branchId)?.branchCode || String(branchId);
  }

  getUserImageUrl(userId?: number | null): string {
    if (!userId) return '';
    return this.userImageMap[String(userId)] || '';
  }

  getUserDisplay(userId?: number | null): string {
    if (!userId) return '-';
    return this.userDisplayMap[userId] || `USER-${userId}`;
  }

  onExport(type: 'csv' | 'excel' | 'pdf'): void {
    const data = this.getExportableAssignments();

    if (!data.length) {
      Swal.fire('No data', 'No assignment data to export.', 'warning');
      return;
    }

    if (type === 'csv') this.exportCSV(data);
    if (type === 'excel') this.exportExcel(data);
    if (type === 'pdf') this.exportPDF(data);
  }

  private getExportableAssignments(): BranchAssignmentResponse[] {
    const hasFilter =
      !!this.filters.branchSearch?.trim() ||
      !!this.filters.status?.trim();

    return hasFilter ? this.filteredAssignments : this.allAssignments;
  }

  private exportCSV(data: BranchAssignmentResponse[]): void {
    const headers = [
      'Branch Code',
      'Branch Name',
      'User ID',
      'Role',
      'From Date',
      'To Date',
      'Primary',
      'Status'
    ];

    const rows = data.map(a => [
      this.getBranchCode(a.branchId),
      this.getBranchName(a.branchId),
      a.userId,
      a.assignmentRole,
      a.fromDate || '',
      a.toDate || 'Present',
      a.isPrimary ? 'YES' : 'NO',
      a.status
    ]);

    const csvContent = [headers, ...rows]
      .map(row => row.map(value => `"${String(value ?? '').replace(/"/g, '""')}"`).join(','))
      .join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'branch-assignments.csv';
    link.click();

    Swal.fire('Exported', 'CSV file downloaded.', 'success');
  }

  private exportExcel(data: BranchAssignmentResponse[]): void {
    import('xlsx').then(xlsx => {
      const rows = data.map(a => ({
        'Branch Code': this.getBranchCode(a.branchId),
        'Branch Name': this.getBranchName(a.branchId),
        'User ID': a.userId,
        'Role': a.assignmentRole,
        'From Date': a.fromDate || '',
        'To Date': a.toDate || 'Present',
        'Primary': a.isPrimary ? 'YES' : 'NO',
        'Status': a.status
      }));

      const worksheet = xlsx.utils.json_to_sheet(rows);
      const workbook = { Sheets: { 'Assignments': worksheet }, SheetNames: ['Assignments'] };

      const excelBuffer = xlsx.write(workbook, { bookType: 'xlsx', type: 'array' });
      const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });

      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = 'branch-assignments.xlsx';
      link.click();

      Swal.fire('Exported', 'Excel file downloaded.', 'success');
    });
  }

  private exportPDF(data: BranchAssignmentResponse[]): void {
    import('jspdf').then(jsPDF => {
      import('jspdf-autotable').then(() => {
        const doc = new jsPDF.default('l');

        const rows = data.map(a => [
          this.getBranchCode(a.branchId),
          this.getBranchName(a.branchId),
          a.userId,
          a.assignmentRole,
          a.fromDate || '',
          a.toDate || 'Present',
          a.isPrimary ? 'YES' : 'NO',
          a.status
        ]);

        (doc as any).autoTable({
          head: [[
            'Branch Code', 'Branch Name', 'User ID', 'Role', 'From', 'To', 'Primary', 'Status'
          ]],
          body: rows,
          styles: { fontSize: 8 }
        });

        doc.save('branch-assignments.pdf');
        Swal.fire('Exported', 'PDF downloaded.', 'success');
      });
    });
  }

  private validateForm(): boolean {
    if (!this.form.branchId || Number(this.form.branchId) <= 0) {
      Swal.fire('Validation', 'Please select a branch.', 'warning');
      return false;
    }

    if (!this.form.userId || Number(this.form.userId) <= 0) {
      Swal.fire('Validation', 'Please enter a valid user ID.', 'warning');
      return false;
    }

    if (!this.form.assignmentRole) {
      Swal.fire('Validation', 'Please select assignment role.', 'warning');
      return false;
    }

    if (!this.form.fromDate) {
      Swal.fire('Validation', 'From date is required.', 'warning');
      return false;
    }

    if (this.form.toDate && this.form.fromDate > this.form.toDate) {
      Swal.fire('Validation', 'From date cannot be after To date.', 'warning');
      return false;
    }

    return true;
  }

  private loadUsers(): void {
    this.userApi.getAll().subscribe({
      next: users => {
        this.userImageMap = this.buildUserImageMap(users || []);
      },
      error: () => {
        this.userImageMap = {};
      }
    });
  }

  private buildUserImageMap(users: UserResponse[]): Record<string, string> {
    return users.reduce<Record<string, string>>((acc, user) => {
      if (user.id && user.profileImageName) {
        acc[String(user.id)] = this.fileUploadService.resolveImageUrl(user.profileImageName);
      }
      if (user.id) {
        this.userDisplayMap[user.id] = user.fullName || user.username;
      }
      return acc;
    }, {});
  }

  private applyFiltersAndPaging(resetPage: boolean): void {
    if (resetPage) this.page = 1;

    const branchKeyword = (this.filters.branchSearch || '').trim().toLowerCase();
    const status = (this.filters.status || '').trim();

    const filtered = this.allAssignments.filter(a => {
      const branchName = this.getBranchName(a.branchId).toLowerCase();
      const branchCode = this.getBranchCode(a.branchId).toLowerCase();

      const matchBranch =
        !branchKeyword ||
        branchName.includes(branchKeyword) ||
        branchCode.includes(branchKeyword) ||
        String(a.branchId).includes(branchKeyword) ||
        String(a.userId).includes(branchKeyword) ||
        (a.assignmentRole || '').toLowerCase().includes(branchKeyword);

      const matchStatus = !status || a.status === status;

      return matchBranch && matchStatus;
    });

    this.filteredAssignments = filtered;
    this.total = filtered.length;

    const start = (this.page - 1) * this.pageSize;
    this.assignments = filtered.slice(start, start + this.pageSize);
  }
}
