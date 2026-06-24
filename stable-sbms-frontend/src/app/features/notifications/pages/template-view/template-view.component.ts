import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { NotificationLogResponse, NotificationTemplateResponse, formatEnumLabel } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-template-view',
  templateUrl: './template-view.component.html',
  styleUrls: ['./template-view.component.scss']
})
export class TemplateViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: NotificationTemplateResponse | null = null;
  recentLogs: NotificationLogResponse[] = [];

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
    forkJoin({
      template: this.notificationService.getTemplateById(id),
      logs: this.notificationService.getLogs()
    }).subscribe({
      next: ({ template, logs }) => {
        this.item = template;
        this.recentLogs = (logs || []).filter(log => log.templateId === id).slice(0, 8);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load template view.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/notifications/templates']);
  }

  edit(): void {
    if (!this.item) return;
    this.router.navigate(['/notifications/templates', this.item.id, 'edit']);
  }

  openLogs(): void {
    if (!this.item) return;
    this.router.navigate(['/notifications/logs'], { queryParams: { keyword: this.item.templateCode } });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
