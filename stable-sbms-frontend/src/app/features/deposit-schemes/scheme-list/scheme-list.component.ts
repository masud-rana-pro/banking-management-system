import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { AccessControlService } from 'src/app/core/services/access-control.service';
import { TableExportService } from 'src/app/core/services/table-export.service';

import { DepositSchemeEnrollmentResponse, DepositSchemeResponse, formatEnumLabel } from '../models/deposit-scheme.model';
import { DepositSchemeService } from '../services/deposit-scheme.service';

@Component({
  selector: 'app-scheme-list',
  templateUrl: './scheme-list.component.html',
  styleUrls: ['./scheme-list.component.scss']
})
export class SchemeListComponent implements OnInit {

  loading = false;
  allItems: DepositSchemeResponse[] = [];
  items: DepositSchemeResponse[] = [];
  enrollments: DepositSchemeEnrollmentResponse[] = [];

  filters = {
    search: '',
    status: '',
    schemeType: ''
  };

  page = 1;
  pageSize = 10;
  total = 0;

  constructor(
    private depositSchemeApi: DepositSchemeService,
    private router: Router,
    public accessControl: AccessControlService,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    Promise.all([
      this.depositSchemeApi.getSchemes().toPromise(),
      this.depositSchemeApi.getEnrollments().toPromise()
    ]).then(([schemes, enrollments]) => {
      this.allItems = schemes || [];
      this.enrollments = enrollments || [];
      this.applyFilters();
      this.loading = false;
    }).catch(err => {
      console.error(err);
      this.loading = false;
      Swal.fire('Error', 'Failed to load deposit schemes.', 'error');
    });
  }

  get activeCount(): number {
    return this.allItems.filter(item => item.status === 'ACTIVE').length;
  }

  get archivedCount(): number {
    return this.allItems.filter(item => item.status === 'ARCHIVED').length;
  }

  get totalEnrollmentCount(): number {
    return this.enrollments.length;
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.schemeCode.toLowerCase().includes(keyword)
        || item.schemeName.toLowerCase().includes(keyword)
        || item.schemeType.toLowerCase().includes(keyword)
        || item.profitFrequency.toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      const matchesType = !this.filters.schemeType || item.schemeType === this.filters.schemeType;
      return matchesKeyword && matchesStatus && matchesType;
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
    this.filters = { search: '', status: '', schemeType: '' };
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
      Swal.fire('No data', 'No deposit scheme data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Deposit Scheme List',
      'Al-Barakah Shariah Banking Management System',
      ['Scheme Code', 'Scheme Name', 'Type', 'Tenure', 'Installment', 'Profit', 'Frequency', 'Status'],
      data.map(item => [
        item.schemeCode,
        item.schemeName,
        item.schemeType,
        `${item.tenureMonths} Months`,
        String(item.minimumInstallment ?? ''),
        `${item.profitRatio ?? 0}%`,
        item.profitFrequency,
        item.status
      ])
    );
  }

  onExport(format: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No deposit scheme data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Deposit Schemes',
      'deposit-schemes',
      ['Scheme Code', 'Scheme Name', 'Type', 'Tenure', 'Installment', 'Profit', 'Frequency', 'Status'],
      data.map(item => [
        item.schemeCode,
        item.schemeName,
        item.schemeType,
        `${item.tenureMonths} Months`,
        String(item.minimumInstallment ?? ''),
        `${item.profitRatio ?? 0}%`,
        item.profitFrequency,
        item.status
      ]),
      format as 'csv' | 'excel' | 'pdf'
    );
  }

  onView(item: DepositSchemeResponse): void {
    this.router.navigate(['/deposit-schemes', item.id]);
  }

  onEdit(item: DepositSchemeResponse): void {
    this.router.navigate(['/deposit-schemes', item.id, 'edit']);
  }

  onEnroll(item: DepositSchemeResponse): void {
    this.router.navigate(['/deposit-schemes/enrollments/new'], { queryParams: { schemeId: item.id } });
  }

  onCalculate(item: DepositSchemeResponse): void {
    this.router.navigate(['/calculations/simulator'], {
      queryParams: {
        sourceModule: 'DEPOSIT_SCHEME',
        productType: item.schemeType,
        principalAmount: item.minimumInstallment,
        ratePercent: item.profitRatio,
        tenureMonths: item.tenureMonths,
        frequency: item.profitFrequency,
        schemeId: item.id,
        sourceName: `${item.schemeCode} - ${item.schemeName}`,
        returnRoute: '/deposit-schemes/list'
      }
    });
  }

  onSchedule(item: DepositSchemeResponse): void {
    const enrollment = this.firstEnrollmentForScheme(item.id);
    if (!enrollment) {
      Swal.fire('No enrollment', 'Create an enrollment first to view schedule.', 'warning');
      return;
    }
    this.router.navigate(['/deposit-schemes/enrollments', enrollment.id, 'schedule']);
  }

  onProfit(item: DepositSchemeResponse): void {
    const enrollment = this.firstEnrollmentForScheme(item.id);
    if (!enrollment) {
      Swal.fire('No enrollment', 'Create an enrollment first to view profit distribution.', 'warning');
      return;
    }
    this.router.navigate(['/deposit-schemes/enrollments', enrollment.id, 'profit']);
  }

  toggleArchive(item: DepositSchemeResponse): void {
    const action$ = item.status === 'ARCHIVED'
      ? this.depositSchemeApi.restoreScheme(item.id)
      : this.depositSchemeApi.archiveScheme(item.id);

    action$.subscribe({
      next: () => {
        Swal.fire('Success', `Scheme ${item.status === 'ARCHIVED' ? 'restored' : 'archived'} successfully.`, 'success');
        this.load();
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Action failed.', 'error')
    });
  }

  firstEnrollmentForScheme(schemeId: number): DepositSchemeEnrollmentResponse | undefined {
    return this.enrollments.find(item => item.schemeId === schemeId);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  private getExportableData(): DepositSchemeResponse[] {
    const keyword = this.filters.search.trim().toLowerCase();
    return this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.schemeCode.toLowerCase().includes(keyword)
        || item.schemeName.toLowerCase().includes(keyword)
        || item.schemeType.toLowerCase().includes(keyword)
        || item.profitFrequency.toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      const matchesType = !this.filters.schemeType || item.schemeType === this.filters.schemeType;
      return matchesKeyword && matchesStatus && matchesType;
    });
  }
}
