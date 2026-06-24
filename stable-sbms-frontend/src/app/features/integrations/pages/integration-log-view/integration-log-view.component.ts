import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { IntegrationExecutionLogResponse, formatEnumLabel } from '../../models/integration.model';
import { IntegrationService } from '../../services/integration.service';

@Component({
  selector: 'app-integration-log-view',
  templateUrl: './integration-log-view.component.html',
  styleUrls: ['./integration-log-view.component.scss']
})
export class IntegrationLogViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: IntegrationExecutionLogResponse | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private integrationService: IntegrationService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    if (this.id) this.load(this.id);
  }

  load(id: number): void {
    this.loading = true;
    this.integrationService.getLogById(id).subscribe({
      next: data => {
        this.item = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load integration log view.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/integrations/logs']);
  }

  openProvider(): void {
    if (!this.item) return;
    this.router.navigate(['/integrations/providers', this.item.providerId]);
  }

  retry(): void {
    if (!this.item) return;
    this.integrationService.retryLog(this.item.id).subscribe({
      next: data => {
        this.item = data;
        Swal.fire('Success', 'Integration retry queued successfully.', 'success');
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
