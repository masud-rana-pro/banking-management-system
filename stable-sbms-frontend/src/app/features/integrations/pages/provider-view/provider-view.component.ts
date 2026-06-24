import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { IntegrationExecutionLogResponse, IntegrationProviderResponse, formatEnumLabel } from '../../models/integration.model';
import { IntegrationService } from '../../services/integration.service';

@Component({
  selector: 'app-provider-view',
  templateUrl: './provider-view.component.html',
  styleUrls: ['./provider-view.component.scss']
})
export class ProviderViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: IntegrationProviderResponse | null = null;
  logs: IntegrationExecutionLogResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private integrationService: IntegrationService,
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
      provider: this.integrationService.getProviderById(id),
      logs: this.integrationService.getLogs({ providerId: id })
    }).subscribe({
      next: ({ provider, logs }) => {
        this.item = provider;
        this.logs = logs || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load provider view.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/integrations/providers']);
  }

  edit(): void {
    if (!this.item) return;
    this.router.navigate(['/integrations/providers', this.item.id, 'edit']);
  }

  openLogs(): void {
    if (!this.item) return;
    this.router.navigate(['/integrations/logs'], { queryParams: { providerId: this.item.id } });
  }

  openTest(): void {
    if (!this.item) return;
    this.router.navigate(['/integrations/provider-test'], { queryParams: { providerId: this.item.id } });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
