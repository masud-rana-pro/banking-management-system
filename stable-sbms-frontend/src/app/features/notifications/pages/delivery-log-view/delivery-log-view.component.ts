import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { NotificationLogResponse, formatEnumLabel } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-delivery-log-view',
  templateUrl: './delivery-log-view.component.html',
  styleUrls: ['./delivery-log-view.component.scss']
})
export class DeliveryLogViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: NotificationLogResponse | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    if (this.id) {
      this.load(this.id);
    }
  }

  load(id: number): void {
    this.loading = true;
    this.notificationService.getLogById(id).subscribe({
      next: data => {
        this.item = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load notification log.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/notifications/logs']);
  }

  retry(): void {
    if (!this.item) return;
    this.notificationService.retryLog(this.item.id).subscribe({
      next: data => {
        this.item = data;
        Swal.fire('Success', 'Notification retry queued successfully.', 'success');
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to retry notification log.', 'error');
      }
    });
  }

  openTemplate(): void {
    if (!this.item) return;
    this.router.navigate(['/notifications/templates', this.item.templateId]);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
