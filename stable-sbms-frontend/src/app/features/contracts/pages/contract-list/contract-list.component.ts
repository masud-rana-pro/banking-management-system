import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { ContractResponse, ContractTemplateResponse, formatEnumLabel } from '../../models/contract.model';
import { ContractService } from '../../services/contract.service';

@Component({
  selector: 'app-contract-list',
  templateUrl: './contract-list.component.html',
  styleUrls: ['./contract-list.component.scss']
})
export class ContractListComponent implements OnInit {
  readonly viewStorageKey = 'sbms.contract-list.view-mode';

  loading = false;
  allItems: ContractResponse[] = [];
  items: ContractResponse[] = [];
  templates: ContractTemplateResponse[] = [];
  customers: CustomerResponse[] = [];
  customerImageMap: Record<number, string> = {};
  page = 1;
  pageSize = 10;
  total = 0;
  viewMode: 'list' | 'grid' = 'list';
  filters = {
    search: '',
    templateId: '',
    customerId: '',
    referenceModule: '',
    contractStatus: ''
  };

  constructor(
    private contractService: ContractService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private route: ActivatedRoute,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.viewMode = (localStorage.getItem(this.viewStorageKey) as 'list' | 'grid') || 'list';
    this.filters.customerId = this.route.snapshot.queryParamMap.get('customerId') || '';
    this.filters.referenceModule = this.route.snapshot.queryParamMap.get('referenceModule') || '';
    this.loadLookups();
    this.load();
  }

  setViewMode(mode: 'list' | 'grid'): void {
    this.viewMode = mode;
    localStorage.setItem(this.viewStorageKey, mode);
  }

  loadLookups(): void {
    this.contractService.getTemplates().subscribe(data => this.templates = data);
    this.customerService.getAll().subscribe(data => {
      this.customers = data;
      this.customerImageMap = this.buildCustomerImageMap(data);
    });
  }

  load(): void {
    this.loading = true;
    this.contractService.getContracts().subscribe({
      next: data => {
        this.allItems = data;
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load contract list.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.contractNo.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.templateCode.toLowerCase().includes(keyword)
        || item.templateName.toLowerCase().includes(keyword);
      const matchesTemplate = !this.filters.templateId || String(item.templateId) === String(this.filters.templateId);
      const matchesCustomer = !this.filters.customerId || String(item.customerId) === String(this.filters.customerId);
      const matchesModule = !this.filters.referenceModule || item.referenceModule === this.filters.referenceModule;
      const matchesStatus = !this.filters.contractStatus || item.contractStatus === this.filters.contractStatus;
      return matchesKeyword && matchesTemplate && matchesCustomer && matchesModule && matchesStatus;
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
    this.filters = { search: '', templateId: '', customerId: '', referenceModule: '', contractStatus: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  onView(item: ContractResponse): void {
    this.router.navigate(['/contracts', item.id]);
  }

  onSign(item: ContractResponse): void {
    this.router.navigate(['/contracts', item.id, 'sign']);
  }

  onVersions(item: ContractResponse): void {
    this.router.navigate(['/contracts', item.id, 'versions']);
  }

  onGenerate(item?: ContractResponse): void {
    if (item) {
      this.router.navigate(['/contracts/generate'], {
        queryParams: {
          templateId: item.templateId,
          customerId: item.customerId,
          referenceModule: item.referenceModule,
          referenceId: item.referenceId
        }
      });
      return;
    }
    this.router.navigate(['/contracts/generate']);
  }

  onShariahCases(item?: ContractResponse): void {
    if (item) {
      this.router.navigate(['/shariah/cases'], {
        queryParams: {
          referenceModule: 'CONTRACT'
        }
      });
      return;
    }
    this.router.navigate(['/shariah/cases']);
  }

  onShariahSubmission(item: ContractResponse): void {
    this.router.navigate(['/shariah/cases'], {
      queryParams: {
        referenceModule: 'CONTRACT',
        referenceId: item.id,
        create: 1
      }
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getCustomerImageUrl(customerId?: number | null): string {
    if (!customerId) {
      return '';
    }
    return this.customerImageMap[customerId] || '';
  }

  get draftCount(): number {
    return this.allItems.filter(item => item.contractStatus === 'DRAFT').length;
  }

  get activeCount(): number {
    return this.allItems.filter(item => item.contractStatus === 'ACTIVE').length;
  }

  get lockedCount(): number {
    return this.allItems.filter(item => item.contractStatus === 'LOCKED').length;
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, customer) => {
      if (customer.id && customer.profileImageName) {
        acc[customer.id] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {} as Record<number, string>);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  canAny(permissionCodes: string[]): boolean {
    return this.accessControl.hasAnyPermission(permissionCodes);
  }
}
