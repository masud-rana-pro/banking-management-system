import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { AccessControlService } from 'src/app/core/services/access-control.service';
import { BranchApiService } from '../services/branch-api.service';
import { BranchResponse } from '../models/branch.model';
import { TableExportService } from 'src/app/core/services/table-export.service';
@Component({
  selector: 'app-branch-list',
  templateUrl: './branch-list.component.html',
  styleUrls: ['./branch-list.component.scss']
})
export class BranchListComponent implements OnInit {

  allBranches: BranchResponse[] = [];
  branches: BranchResponse[] = [];
  filteredBranches: BranchResponse[] = [];

  loading = false;

  page = 1;
  pageSize = 10;
  total = 0;

  totalAll = 0;
  activeCount = 0;
  inactiveCount = 0;

  filters = {
    search: '',
    branchType: '',
    status: ''
  };

  branchTypeOptions = [
    { value: '', label: 'All Branch Types' },
    { value: 'MAIN', label: 'Main Branch' },
    { value: 'URBAN', label: 'Urban Branch' },
    { value: 'CORPORATE', label: 'Corporate Branch' },
    { value: 'INDUSTRIAL', label: 'Industrial Branch' }
  ];

  statusOptions = [
    { value: '', label: 'All Status' },
    { value: 'ACTIVE', label: 'ACTIVE' },
    { value: 'INACTIVE', label: 'INACTIVE' }
  ];

  constructor(
    private branchApi: BranchApiService,
    private router: Router,
    public accessControl: AccessControlService,
    private tableExport: TableExportService
  ) { }
  ngOnInit(): void {
    this.loadBranches();
  }

  loadBranches(): void {
    this.loading = true;

    this.branchApi.getAll(this.filters.search, this.filters.status).subscribe({
      next: (data) => {
        this.allBranches = data || [];
        this.calculateSummary();
        this.applyFiltersAndPaging(true);
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load branches', err);
        this.allBranches = [];
        this.calculateSummary();
        this.applyFiltersAndPaging(true);
        this.loading = false;

        Swal.fire({
          icon: 'error',
          title: 'Failed to load branches',
          text: 'Please check backend API and try again.'
        });
      }
    });
  }

  onSearch(): void {
    this.loadBranches();
  }

  onReset(): void {
    this.filters = { search: '', branchType: '', status: '' };
    this.loadBranches();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFiltersAndPaging(false);
  }

  onView(branch: BranchResponse): void {
    this.router.navigate(['/branches', branch.id]);
  }

  openDashboard(): void {
    this.router.navigate(['/branches/dashboard']);
  }

  goAssignments(): void {
    this.router.navigate(['/branches/assignments']);
  }

  goOpenVault(): void {
    this.router.navigate(['/branches/vault/open']);
  }

  goEodSummary(): void {
    this.router.navigate(['/branches/eod-summary']);
  }

  onEdit(branch: BranchResponse): void {
    this.router.navigate(['/branches', branch.id, 'edit']);
  }

  onStatement(branch: BranchResponse): void {
    this.router.navigate(['/statement/branch/request'], { queryParams: { branchId: branch.id } });
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  onArchive(branch: BranchResponse): void {
    Swal.fire({
      icon: 'warning',
      title: 'Archive branch?',
      text: `${branch.branchName} will be hidden from active branch list.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, archive',
      cancelButtonText: 'Cancel'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.branchApi.archive(branch.id).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Archived',
            text: 'Branch archived successfully.',
            timer: 1400,
            showConfirmButton: false
          });

          this.loadBranches();
        },
        error: (err) => {
          console.error('Archive failed', err);
          Swal.fire({
            icon: 'error',
            title: 'Archive failed',
            text: 'Please check backend validation and try again.'
          });
        }
      });
    });
  }
  onRestore(branch: BranchResponse): void {
    Swal.fire({
      icon: 'question',
      title: 'Restore branch?',
      text: `${branch.branchName} will be restored as active branch.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, restore',
      cancelButtonText: 'Cancel'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.branchApi.restore(branch.id).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Restored',
            text: 'Branch restored successfully.',
            timer: 1400,
            showConfirmButton: false
          });

          this.loadBranches();
        },
        error: err => {
          console.error('Restore failed', err);
          Swal.fire({
            icon: 'error',
            title: 'Restore failed',
            text: err?.error?.message || 'Please check backend API and try again.'
          });
        }
      });
    });
  }
  private applyFiltersAndPaging(resetPage: boolean): void {
    if (resetPage) this.page = 1;

    const search = (this.filters.search || '').trim().toLowerCase();
    const branchType = (this.filters.branchType || '').trim();
    const status = (this.filters.status || '').trim();

    const filtered = this.allBranches.filter(b => {
      const matchSearch =
        !search ||
        (b.branchCode || '').toLowerCase().includes(search) ||
        (b.branchName || '').toLowerCase().includes(search) ||
        (b.branchShortName || '').toLowerCase().includes(search) ||
        (b.routingNo || '').toLowerCase().includes(search) ||
        (b.email || '').toLowerCase().includes(search) ||
        (b.mobile || '').toLowerCase().includes(search);

      const matchType = !branchType || b.branchType === branchType;
      const matchStatus = !status || b.status === status;

      return matchSearch && matchType && matchStatus;
    });

    this.filteredBranches = filtered;
    this.total = filtered.length;

    const start = (this.page - 1) * this.pageSize;
    this.branches = filtered.slice(start, start + this.pageSize);
  }

  private calculateSummary(): void {
    this.totalAll = this.allBranches.length;
    this.activeCount = this.allBranches.filter(b => b.status === 'ACTIVE').length;
    this.inactiveCount = this.allBranches.filter(b => b.status === 'INACTIVE').length;
  }

  onPrint(): void {
    const data = this.getExportableBranches();

    if (!data || data.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'No data',
        text: 'No branch data to print.'
      });
      return;
    }
    this.tableExport.printTableDocument(
      'Branch List',
      'Al-Barakah Shariah Banking Management System',
      ['Code', 'Branch Name', 'Type', 'Routing', 'Contact', 'Email', 'Opened', 'Status'],
      data.map(b => [
        b.branchCode,
        b.branchName,
        b.branchType,
        b.routingNo,
        b.mobile || b.phone || '',
        b.email || '',
        b.openedDate || '',
        b.status
      ])
    );
  }

  onExport(type: 'csv' | 'excel' | 'pdf'): void {

    const data = this.getExportableBranches();

    if (!data || data.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'No data',
        text: 'No branch data to export'
      });
      return;
    }

    this.tableExport.exportTable(
      'Branches',
      'branch-list',
      ['Code', 'Branch Name', 'Type', 'Routing', 'Contact', 'Email', 'Opened', 'Status'],
      data.map(b => [
        b.branchCode,
        b.branchName,
        b.branchType,
        b.routingNo,
        b.mobile || b.phone || '',
        b.email || '',
        b.openedDate || '',
        b.status
      ]),
      type
    );
  }
  private getExportableBranches(): BranchResponse[] {
    const hasActiveFilter =
      !!this.filters.search?.trim() ||
      !!this.filters.branchType?.trim() ||
      !!this.filters.status?.trim();

    return hasActiveFilter ? this.filteredBranches : this.allBranches;
  }
}
