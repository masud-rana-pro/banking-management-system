import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { NotificationTemplateResponse, formatEnumLabel } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-template-list',
  templateUrl: './template-list.component.html',
  styleUrls: ['./template-list.component.scss']
})
export class TemplateListComponent implements OnInit {

  loading = false;
  allItems: NotificationTemplateResponse[] = [];
  items: NotificationTemplateResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  filters = {
    search: '',
    channelType: '',
    status: ''
  };

  constructor(
    private notificationService: NotificationService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.notificationService.getTemplates().subscribe({
      next: data => {
        this.allItems = data || [];
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load notification templates.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.templateCode.toLowerCase().includes(keyword)
        || item.templateName.toLowerCase().includes(keyword)
        || (item.subjectText || '').toLowerCase().includes(keyword)
        || item.bodyText.toLowerCase().includes(keyword);
      const matchesChannel = !this.filters.channelType || item.channelType === this.filters.channelType;
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      return matchesKeyword && matchesChannel && matchesStatus;
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
    this.filters = { search: '', channelType: '', status: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openNew(): void {
    this.router.navigate(['/notifications/templates/new']);
  }

  openView(item: NotificationTemplateResponse): void {
    this.router.navigate(['/notifications/templates', item.id]);
  }

  openEdit(item: NotificationTemplateResponse): void {
    this.router.navigate(['/notifications/templates', item.id, 'edit']);
  }

  toggleArchive(item: NotificationTemplateResponse): void {
    const request$ = item.status === 'ARCHIVED'
      ? this.notificationService.restoreTemplate(item.id)
      : this.notificationService.archiveTemplate(item.id);

    request$.subscribe({
      next: () => {
        Swal.fire('Success', `Notification template ${item.status === 'ARCHIVED' ? 'restored' : 'archived'} successfully.`, 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to change template status.', 'error');
      }
    });
  }

  get activeCount(): number {
    return this.allItems.filter(item => item.status === 'ACTIVE').length;
  }

  get archivedCount(): number {
    return this.allItems.filter(item => item.status === 'ARCHIVED').length;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
