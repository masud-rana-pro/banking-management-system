import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { TableExportService } from 'src/app/core/services/table-export.service';
import { AccountTypeResponse } from '../../../accounts/models/account.model';
import { AccountService } from '../../../accounts/services/account.service';
import { ProfitRatioResponse, formatEnumLabel } from '../../models/profit.model';
import { ProfitService } from '../../services/profit.service';

@Component({
  selector: 'app-profit-ratio-list',
  templateUrl: './profit-ratio-list.component.html',
  styleUrls: ['./profit-ratio-list.component.scss']
})
export class ProfitRatioListComponent implements OnInit {

  loading = false;
  allItems: ProfitRatioResponse[] = [];
  items: ProfitRatioResponse[] = [];
  accountTypes: AccountTypeResponse[] = [];

  filters = {
    search: '',
    accountTypeId: '',
    status: '',
    activity: ''
  };

  page = 1;
  pageSize = 10;
  total = 0;

  constructor(
    private profitApi: ProfitService,
    private accountApi: AccountService,
    private route: ActivatedRoute,
    private router: Router,
    public accessControl: AccessControlService,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    this.filters.accountTypeId = this.route.snapshot.queryParamMap.get('accountTypeId') || '';
    this.loadData();
  }

  get activeCount(): number {
    return this.allItems.filter(item => item.activeNow).length;
  }

  get archivedCount(): number {
    return this.allItems.filter(item => item.status === 'ARCHIVED').length;
  }

  get accountTypeCoverage(): number {
    return new Set(this.allItems.map(item => item.accountTypeId)).size;
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      ratios: this.profitApi.getRatios(),
      accountTypes: this.accountApi.getAccountTypes()
    }).subscribe({
      next: ({ ratios, accountTypes }) => {
        this.allItems = ratios || [];
        this.accountTypes = (accountTypes || []).filter(item => item.profitApplicable);
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load profit ratio list.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.ratioCode.toLowerCase().includes(keyword)
        || item.accountTypeCode.toLowerCase().includes(keyword)
        || item.accountTypeName.toLowerCase().includes(keyword);
      const matchesType = !this.filters.accountTypeId || String(item.accountTypeId) === this.filters.accountTypeId;
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      const matchesActivity = !this.filters.activity
        || (this.filters.activity === 'ACTIVE_NOW' && item.activeNow)
        || (this.filters.activity === 'INACTIVE' && !item.activeNow);
      return matchesKeyword && matchesType && matchesStatus && matchesActivity;
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
    this.filters = { search: '', accountTypeId: '', status: '', activity: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  onPrint(): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No profit ratio data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Profit Ratio List',
      'Al-Barakah Shariah Banking Management System',
      ['Ratio Code', 'Account Type', 'Ratio (%)', 'Status', 'Active Now'],
      data.map(item => [
        item.ratioCode,
        `${item.accountTypeCode} - ${item.accountTypeName}`,
        String(item.ratioPercent ?? ''),
        item.status,
        item.activeNow ? 'Yes' : 'No'
      ])
    );
  }

  onExport(format: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No profit ratio data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Profit Ratios',
      'profit-ratio-list',
      ['Ratio Code', 'Account Type', 'Ratio (%)', 'Status', 'Active Now'],
      data.map(item => [
        item.ratioCode,
        `${item.accountTypeCode} - ${item.accountTypeName}`,
        String(item.ratioPercent ?? ''),
        item.status,
        item.activeNow ? 'Yes' : 'No'
      ]),
      format as 'csv' | 'excel' | 'pdf'
    );
  }

  onView(item: ProfitRatioResponse): void {
    this.router.navigate(['/profit/ratios', item.id]);
  }

  onEdit(item: ProfitRatioResponse): void {
    this.router.navigate(['/profit/ratios', item.id, 'edit']);
  }

  onCreateSchedule(item: ProfitRatioResponse): void {
    this.router.navigate(['/profit/schedules/new'], { queryParams: { accountTypeId: item.accountTypeId } });
  }

  onRunPosting(item: ProfitRatioResponse): void {
    this.router.navigate(['/profit/postings/run'], { queryParams: { accountTypeId: item.accountTypeId } });
  }

  toggleArchive(item: ProfitRatioResponse): void {
    const restoring = item.status === 'ARCHIVED';
    const action$ = restoring
      ? this.profitApi.restoreRatio(item.id)
      : this.profitApi.archiveRatio(item.id);
    Swal.fire({
      icon: 'question',
      title: restoring ? 'Restore ratio?' : 'Archive ratio?',
      showCancelButton: true,
      confirmButtonText: 'Continue'
    }).then(result => {
      if (!result.isConfirmed) return;
      action$.subscribe({
        next: () => {
          Swal.fire('Success', restoring ? 'Profit ratio restored successfully.' : 'Profit ratio archived successfully.', 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Failed to update profit ratio.', 'error')
      });
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  private getExportableData(): ProfitRatioResponse[] {
    const keyword = this.filters.search.trim().toLowerCase();
    return this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.ratioCode.toLowerCase().includes(keyword)
        || item.accountTypeCode.toLowerCase().includes(keyword)
        || item.accountTypeName.toLowerCase().includes(keyword);
      const matchesType = !this.filters.accountTypeId || String(item.accountTypeId) === this.filters.accountTypeId;
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      const matchesActivity = !this.filters.activity
        || (this.filters.activity === 'ACTIVE_NOW' && item.activeNow)
        || (this.filters.activity === 'INACTIVE' && !item.activeNow);
      return matchesKeyword && matchesType && matchesStatus && matchesActivity;
    });
  }
}
