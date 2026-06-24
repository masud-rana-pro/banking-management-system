import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { NotificationLogResponse, formatEnumLabel } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-retry-queue',
  templateUrl: './retry-queue.component.html',
  styleUrls: ['./retry-queue.component.scss']
})
export class RetryQueueComponent implements OnInit {

  loading = false;
  items: NotificationLogResponse[] = [];

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
    this.notificationService.getLogs().subscribe({
      next: data => {
        this.items = (data || []).filter(item => item.deliveryStatus === 'FAILED' || item.deliveryStatus === 'RETRY_QUEUED');
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load retry queue.', 'error');
      }
    });
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

  print(): void {
    window.print();
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
