import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerResponse, formatEnumLabel } from '../../models/customer.model';
import { CustomerService } from '../../services/customer.service';

@Component({
  selector: 'app-customer-search',
  templateUrl: './customer-search.component.html',
  styleUrls: ['./customer-search.component.scss']
})
export class CustomerSearchComponent {

  keyword = '';
  loading = false;
  searched = false;
  results: CustomerResponse[] = [];
  branches: BranchResponse[] = [];

  constructor(
    private customerApi: CustomerService,
    private branchApi: BranchApiService,
    private router: Router,
    private fileUploadService: FileUploadService
  ) {
    this.branchApi.getAll().subscribe({
      next: data => this.branches = data || [],
      error: err => console.error(err)
    });
  }

  get activeCount(): number {
    return this.results.filter(item => item.customerStatus === 'ACTIVE').length;
  }

  get pendingCount(): number {
    return this.results.filter(item => item.customerStatus === 'PENDING_KYC').length;
  }

  get blockedCount(): number {
    return this.results.filter(item => item.customerStatus === 'BLOCKED').length;
  }

  onSearch(): void {
    this.loading = true;
    this.searched = true;

    forkJoin({
      results: this.customerApi.search(this.keyword.trim())
    }).subscribe({
      next: ({ results }) => {
        this.results = results || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Customer search failed.', 'error');
      }
    });
  }

  onReset(): void {
    this.keyword = '';
    this.results = [];
    this.searched = false;
  }

  onView(item: CustomerResponse): void {
    this.router.navigate(['/customers', item.id]);
  }

  onStatus(item: CustomerResponse): void {
    this.router.navigate(['/customers', item.id, 'status']);
  }

  onManageIdentity(item: CustomerResponse): void {
    this.router.navigate(['/customers', item.id, 'identities']);
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) {
      return 'Unassigned Branch';
    }

    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId || '-'}`;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }
}
