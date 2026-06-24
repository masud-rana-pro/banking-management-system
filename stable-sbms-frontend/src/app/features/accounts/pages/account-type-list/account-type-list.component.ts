import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccountTypeResponse, formatEnumLabel } from '../../models/account.model';
import { AccountService } from '../../services/account.service';

@Component({
  selector: 'app-account-type-list',
  templateUrl: './account-type-list.component.html',
  styleUrls: ['./account-type-list.component.scss']
})
export class AccountTypeListComponent implements OnInit {

  loading = false;
  allItems: AccountTypeResponse[] = [];
  items: AccountTypeResponse[] = [];

  search = '';
  status = '';
  page = 1;
  pageSize = 10;
  total = 0;

  get activeCount(): number {
    return this.allItems.filter(item => item.status === 'ACTIVE').length;
  }

  get profitApplicableCount(): number {
    return this.allItems.filter(item => item.profitApplicable).length;
  }

  get archivedCount(): number {
    return this.allItems.filter(item => item.status === 'ARCHIVED').length;
  }

  ngOnInit(): void {
    this.loadData();
  }

  constructor(
    private accountApi: AccountService,
    private router: Router
  ) {}

  loadData(): void {
    this.loading = true;
    this.accountApi.getAccountTypes().subscribe({
      next: items => {
        this.allItems = items || [];
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load account type list.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.typeCode.toLowerCase().includes(keyword)
        || item.typeName.toLowerCase().includes(keyword)
        || String(item.shariahContractType || '').toLowerCase().includes(keyword)
        || String(item.currencyCode || '').toLowerCase().includes(keyword);
      const matchesStatus = !this.status || item.status === this.status;
      return matchesKeyword && matchesStatus;
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
    this.search = '';
    this.status = '';
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  onView(item: AccountTypeResponse): void {
    this.router.navigate(['/accounts/account-types', item.id]);
  }

  onEdit(item: AccountTypeResponse): void {
    this.router.navigate(['/accounts/account-types', item.id, 'edit']);
  }

  onArchive(item: AccountTypeResponse): void {
    Swal.fire({
      icon: 'question',
      title: 'Archive account type?',
      text: `${item.typeCode} will be archived.`,
      showCancelButton: true,
      confirmButtonText: 'Archive'
    }).then(result => {
      if (!result.isConfirmed) return;
      this.accountApi.archiveAccountType(item.id).subscribe({
        next: () => {
          Swal.fire('Archived', 'Account type archived successfully.', 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Archive failed.', 'error')
      });
    });
  }

  onRestore(item: AccountTypeResponse): void {
    this.accountApi.restoreAccountType(item.id).subscribe({
      next: () => {
        Swal.fire('Restored', 'Account type restored successfully.', 'success');
        this.loadData();
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Restore failed.', 'error')
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
