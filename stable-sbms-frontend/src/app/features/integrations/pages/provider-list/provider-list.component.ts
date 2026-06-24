import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { IntegrationProviderResponse, formatEnumLabel } from '../../models/integration.model';
import { IntegrationService } from '../../services/integration.service';

@Component({
  selector: 'app-provider-list',
  templateUrl: './provider-list.component.html',
  styleUrls: ['./provider-list.component.scss']
})
export class ProviderListComponent implements OnInit {

  loading = false;
  allItems: IntegrationProviderResponse[] = [];
  items: IntegrationProviderResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  filters = {
    keyword: '',
    providerType: '',
    status: ''
  };

  constructor(
    private integrationService: IntegrationService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.integrationService.getProviders().subscribe({
      next: data => {
        this.allItems = data || [];
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load integration providers.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.keyword.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.providerCode.toLowerCase().includes(keyword)
        || item.providerName.toLowerCase().includes(keyword)
        || item.baseUrl.toLowerCase().includes(keyword)
        || (item.username || '').toLowerCase().includes(keyword);
      const matchesType = !this.filters.providerType || item.providerType === this.filters.providerType;
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      return matchesKeyword && matchesType && matchesStatus;
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
    this.filters = { keyword: '', providerType: '', status: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openNew(): void {
    this.router.navigate(['/integrations/providers/new']);
  }

  openView(item: IntegrationProviderResponse): void {
    this.router.navigate(['/integrations/providers', item.id]);
  }

  openEdit(item: IntegrationProviderResponse): void {
    this.router.navigate(['/integrations/providers', item.id, 'edit']);
  }

  openLogs(item: IntegrationProviderResponse): void {
    this.router.navigate(['/integrations/logs'], { queryParams: { providerId: item.id } });
  }

  testProvider(item: IntegrationProviderResponse): void {
    this.router.navigate(['/integrations/provider-test'], { queryParams: { providerId: item.id } });
  }

  toggleArchive(item: IntegrationProviderResponse): void {
    const request$ = item.status === 'ARCHIVED'
      ? this.integrationService.restoreProvider(item.id)
      : this.integrationService.archiveProvider(item.id);

    request$.subscribe({
      next: () => {
        Swal.fire('Success', `Integration provider ${item.status === 'ARCHIVED' ? 'restored' : 'archived'} successfully.`, 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to change provider status.', 'error');
      }
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
