import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { TableExportService } from 'src/app/core/services/table-export.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { ZakatProfileResponse, formatEnumLabel } from '../../models/zakat.model';
import { ZakatService } from '../../services/zakat.service';

@Component({
  selector: 'app-zakat-profile-list',
  templateUrl: './zakat-profile-list.component.html',
  styleUrls: ['./zakat-profile-list.component.scss']
})
export class ZakatProfileListComponent implements OnInit {
  readonly viewStorageKey = 'sbms.zakat-profile-list.view-mode';

  loading = false;
  allItems: ZakatProfileResponse[] = [];
  items: ZakatProfileResponse[] = [];
  customers: CustomerResponse[] = [];
  customerImageMap: Record<number, string> = {};
  page = 1;
  pageSize = 10;
  total = 0;
  viewMode: 'list' | 'grid' = 'list';
  filters = {
    search: '',
    customerId: '',
    zakatYear: ''
  };

  constructor(
    private zakatService: ZakatService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private route: ActivatedRoute,
    private router: Router,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    const savedViewMode = localStorage.getItem(this.viewStorageKey);
    if (savedViewMode === 'list' || savedViewMode === 'grid') {
      this.viewMode = savedViewMode;
    }
    this.filters.customerId = this.route.snapshot.queryParamMap.get('customerId') || '';
    this.loadLookups();
    this.load();
  }

  loadLookups(): void {
    this.customerService.getAll().subscribe(data => {
      this.customers = data;
      this.customerImageMap = this.buildCustomerImageMap(data);
    });
  }

  load(): void {
    this.loading = true;
    this.zakatService.getProfiles().subscribe({
      next: data => {
        this.allItems = data;
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load zakat profiles.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchKeyword = !keyword
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || String(item.zakatYear).includes(keyword);
      const matchCustomer = !this.filters.customerId || String(item.customerId) === String(this.filters.customerId);
      const matchYear = !this.filters.zakatYear || String(item.zakatYear) === String(this.filters.zakatYear);
      return matchKeyword && matchCustomer && matchYear;
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
    this.filters = { search: '', customerId: '', zakatYear: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  setViewMode(mode: 'list' | 'grid'): void {
    this.viewMode = mode;
    localStorage.setItem(this.viewStorageKey, mode);
  }

  openView(item: ZakatProfileResponse): void {
    this.router.navigate(['/zakat/profiles', item.id]);
  }

  openCalc(item?: ZakatProfileResponse): void {
    this.router.navigate(['/zakat/calc-run'], {
      queryParams: item
        ? { profileId: item.id }
        : {}
    });
  }

  openBeneficiary(): void {
    this.router.navigate(['/zakat/beneficiaries/new']);
  }

  openPayout(item?: ZakatProfileResponse): void {
    this.router.navigate(['/zakat/payouts/new'], {
      queryParams: item ? { customerId: item.customerId } : {}
    });
  }

  openFund(): void {
    this.router.navigate(['/zakat/charity-fund']);
  }

  onPrint(): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No zakat profile data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Zakat Profile List',
      'Al-Barakah Shariah Banking Management System',
      ['Customer', 'Zakat Year', 'Payable Amount', 'Status', 'Proof'],
      data.map(item => [
        `${item.customerCode} - ${item.customerName}`,
        String(item.zakatYear),
        String(item.zakatAmount ?? ''),
        item.calculationStatus || 'Generated',
        item.proofDocumentName ? 'Available' : 'Not Available'
      ])
    );
  }

  onExport(type: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No zakat profile data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Zakat Profiles',
      'zakat-profile-list',
      ['Customer', 'Zakat Year', 'Payable Amount', 'Status', 'Proof'],
      data.map(item => [
        `${item.customerCode} - ${item.customerName}`,
        String(item.zakatYear),
        String(item.zakatAmount ?? ''),
        item.calculationStatus || 'Generated',
        item.proofDocumentName ? 'Available' : 'Not Available'
      ]),
      type as 'csv' | 'excel' | 'pdf'
    );
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getProfileTone(item: ZakatProfileResponse): string {
    if (item.calculationStatus === 'DEDUCTED') return 'deducted';
    if (item.calculationStatus === 'BELOW_NISAB') return 'below-nisab';
    if (item.calculationStatus === 'CALCULATED') return 'calculated';
    return 'profiled';
  }

  getCustomerInitial(item: ZakatProfileResponse): string {
    const source = item.customerName || item.customerCode || 'Z';
    return source.charAt(0).toUpperCase();
  }

  getCustomerImageUrl(customerId?: number | null): string {
    if (!customerId) {
      return '';
    }
    return this.customerImageMap[customerId] || '';
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, customer) => {
      if (customer.id && customer.profileImageName) {
        acc[customer.id] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {} as Record<number, string>);
  }

  private getExportableData(): ZakatProfileResponse[] {
    const keyword = this.filters.search.trim().toLowerCase();
    return this.allItems.filter(item => {
      const matchKeyword = !keyword
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || String(item.zakatYear).includes(keyword);
      const matchCustomer = !this.filters.customerId || String(item.customerId) === String(this.filters.customerId);
      const matchYear = !this.filters.zakatYear || String(item.zakatYear) === String(this.filters.zakatYear);
      return matchKeyword && matchCustomer && matchYear;
    });
  }
}
