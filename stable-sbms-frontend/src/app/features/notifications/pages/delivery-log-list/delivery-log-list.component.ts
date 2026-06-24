import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { NotificationLogResponse, formatEnumLabel } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-delivery-log-list',
  templateUrl: './delivery-log-list.component.html',
  styleUrls: ['./delivery-log-list.component.scss']
})
export class DeliveryLogListComponent implements OnInit {

  loading = false;
  allItems: NotificationLogResponse[] = [];
  items: NotificationLogResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  filters = {
    keyword: '',
    deliveryStatus: '',
    channelType: ''
  };

  constructor(
    private notificationService: NotificationService,
    private route: ActivatedRoute,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.filters.keyword = params.get('keyword') || '';
      this.load();
    });
  }

  load(): void {
    this.loading = true;
    this.notificationService.getLogs().subscribe({
      next: data => {
        this.allItems = data || [];
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load notification delivery logs.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.keyword.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.eventCode.toLowerCase().includes(keyword)
        || item.eventName.toLowerCase().includes(keyword)
        || item.templateCode.toLowerCase().includes(keyword)
        || item.templateName.toLowerCase().includes(keyword)
        || item.recipientTo.toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.deliveryStatus || item.deliveryStatus === this.filters.deliveryStatus;
      const matchesChannel = !this.filters.channelType || item.channelType === this.filters.channelType;
      return matchesKeyword && matchesStatus && matchesChannel;
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
    this.filters = { keyword: '', deliveryStatus: '', channelType: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openView(item: NotificationLogResponse): void {
    this.router.navigate(['/notifications/logs', item.id]);
  }

  retry(item: NotificationLogResponse): void {
    this.notificationService.retryLog(item.id).subscribe({
      next: () => {
        Swal.fire('Success', 'Notification retry queued successfully.', 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to retry notification log.', 'error');
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
