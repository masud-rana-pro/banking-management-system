import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { IntegrationExecutionLogResponse, IntegrationProviderResponse, IntegrationProviderTestRequest, formatEnumLabel } from '../../models/integration.model';
import { IntegrationService } from '../../services/integration.service';

@Component({
  selector: 'app-provider-test',
  templateUrl: './provider-test.component.html',
  styleUrls: ['./provider-test.component.scss']
})
export class ProviderTestComponent implements OnInit {

  loading = false;
  providers: IntegrationProviderResponse[] = [];
  result: IntegrationExecutionLogResponse | null = null;
  form = {
    providerId: null as number | null,
    referenceModule: 'INTEGRATION_PROVIDER_TEST',
    referenceId: null as number | null,
    requestPayload: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private integrationService: IntegrationService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadProviders();
  }

  loadProviders(): void {
    this.loading = true;
    this.integrationService.getProviders({ status: 'ACTIVE' }).subscribe({
      next: data => {
        this.providers = data || [];
        const providerId = this.route.snapshot.queryParamMap.get('providerId');
        this.form.providerId = providerId ? Number(providerId) : (this.providers[0]?.id || null);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load providers for test execution.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.form.providerId) {
      Swal.fire('Warning', 'Provider selection is required.', 'warning');
      return;
    }

    this.loading = true;
    const payload: IntegrationProviderTestRequest = {
      referenceModule: this.form.referenceModule?.trim() || null,
      referenceId: this.form.referenceId,
      requestPayload: this.form.requestPayload?.trim() || null
    };

    this.integrationService.testProvider(this.form.providerId, payload).subscribe({
      next: data => {
        this.result = data;
        this.loading = false;
        Swal.fire('Success', 'Provider test executed successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', err?.error?.message || 'Failed to execute provider test.', 'error');
      }
    });
  }

  openLog(): void {
    if (!this.result) return;
    this.router.navigate(['/integrations/logs', this.result.id]);
  }

  openProvider(): void {
    if (!this.form.providerId) return;
    this.router.navigate(['/integrations/providers', this.form.providerId]);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
