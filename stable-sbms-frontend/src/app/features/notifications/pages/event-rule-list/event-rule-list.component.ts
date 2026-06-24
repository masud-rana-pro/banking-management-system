import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { NotificationEventResponse, formatEnumLabel } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-event-rule-list',
  templateUrl: './event-rule-list.component.html',
  styleUrls: ['./event-rule-list.component.scss']
})
export class EventRuleListComponent implements OnInit {

  loading = false;
  allItems: NotificationEventResponse[] = [];
  items: NotificationEventResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  filters = {
    search: '',
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
    this.notificationService.getEventRules().subscribe({
      next: data => {
        this.allItems = data || [];
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load notification event rules.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.eventCode.toLowerCase().includes(keyword)
        || item.eventName.toLowerCase().includes(keyword)
        || (item.referenceModule || '').toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
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
    this.filters = { search: '', status: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openNew(): void {
    this.router.navigate(['/notifications/event-rules/new']);
  }

  openLogs(item: NotificationEventResponse): void {
    this.router.navigate(['/notifications/logs'], { queryParams: { keyword: item.eventCode } });
  }

  onPrint(): void {
    window.print();
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
