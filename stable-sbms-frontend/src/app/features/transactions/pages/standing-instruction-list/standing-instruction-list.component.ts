import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { StandingInstructionResponse, formatEnumLabel } from '../../models/transaction.model';
import { TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-standing-instruction-list',
  templateUrl: './standing-instruction-list.component.html',
  styleUrls: ['./standing-instruction-list.component.scss']
})
export class StandingInstructionListComponent implements OnInit {

  loading = false;
  allItems: StandingInstructionResponse[] = [];
  items: StandingInstructionResponse[] = [];
  branches: BranchResponse[] = [];
  filters = { search: '', branchId: '' };
  page = 1;
  pageSize = 10;
  total = 0;

  constructor(
    private transactionApi: TransactionService,
    private branchApi: BranchApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      items: this.transactionApi.getStandingInstructions(),
      branches: this.branchApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ items, branches }) => {
        this.allItems = items || [];
        this.branches = branches || [];
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load standing instruction list.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.instructionCode.toLowerCase().includes(keyword)
        || String(item.fromAccountNumber || '').toLowerCase().includes(keyword)
        || String(item.toAccountNumber || '').toLowerCase().includes(keyword)
        || String(item.fromCustomerName || '').toLowerCase().includes(keyword)
        || String(item.toCustomerName || '').toLowerCase().includes(keyword);
      const matchesBranch = !this.filters.branchId || String(item.branchId || '') === this.filters.branchId;
      return matchesKeyword && matchesBranch;
    });
    this.total = filtered.length;
    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  onSearch(): void {
    this.page = 1;
    this.applyFilters();
  }

  onReset(): void {
    this.filters = { search: '', branchId: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openAccount(id: number): void {
    this.router.navigate(['/accounts', id]);
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) return 'Unassigned Branch';
    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId}`;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
