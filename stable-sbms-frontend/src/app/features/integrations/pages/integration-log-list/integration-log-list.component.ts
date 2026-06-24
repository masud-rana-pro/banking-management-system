import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { IntegrationExecutionLogResponse, formatEnumLabel } from '../../models/integration.model';
import { IntegrationService } from '../../services/integration.service';

@Component({
  selector: 'app-integration-log-list',
  templateUrl: './integration-log-list.component.html',
  styleUrls: ['./integration-log-list.component.scss']
})
export class IntegrationLogListComponent implements OnInit {

  loading = false;
  allItems: IntegrationExecutionLogResponse[] = [];
  items: IntegrationExecutionLogResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  filters = {
    keyword: '',
    executionStatus: '',
    providerId: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private integrationService: IntegrationService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.filters.keyword = params.get('keyword') || '';
      this.filters.executionStatus = params.get('executionStatus') || '';
      this.filters.providerId = params.get('providerId') || '';
      this.load();
    });
  }

  load(): void {
    this.loading = true;
    this.integrationService.getLogs().subscribe({
      next: data => {
        this.allItems = data || [];
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load integration logs.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.keyword.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.providerCode.toLowerCase().includes(keyword)
        || item.providerName.toLowerCase().includes(keyword)
        || (item.referenceModule || '').toLowerCase().includes(keyword)
        || (item.requestPayload || '').toLowerCase().includes(keyword)
        || (item.responsePayload || '').toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.executionStatus || item.executionStatus === this.filters.executionStatus;
      const matchesProvider = !this.filters.providerId || item.providerId === Number(this.filters.providerId);
      return matchesKeyword && matchesStatus && matchesProvider;
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
    this.filters = { keyword: '', executionStatus: '', providerId: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openView(item: IntegrationExecutionLogResponse): void {
    this.router.navigate(['/integrations/logs', item.id]);
  }

  openProvider(item: IntegrationExecutionLogResponse): void {
    this.router.navigate(['/integrations/providers', item.providerId]);
  }

  retry(item: IntegrationExecutionLogResponse): void {
    this.integrationService.retryLog(item.id).subscribe({
      next: () => {
        Swal.fire('Success', 'Integration execution retry queued successfully.', 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to queue retry.', 'error');
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
