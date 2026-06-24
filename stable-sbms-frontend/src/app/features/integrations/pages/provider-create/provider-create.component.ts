import { Component } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { IntegrationProviderRequest } from '../../models/integration.model';
import { IntegrationService } from '../../services/integration.service';

@Component({
  selector: 'app-provider-create',
  templateUrl: './provider-create.component.html',
  styleUrls: ['./provider-create.component.scss']
})
export class ProviderCreateComponent {

  saving = false;
  form: IntegrationProviderRequest = {
    providerCode: '',
    providerName: '',
    providerType: '',
    baseUrl: '',
    authType: '',
    apiKey: '',
    username: '',
    password: '',
    timeoutSec: 30
  };

  constructor(
    private integrationService: IntegrationService,
    private router: Router
  ) {}

  submit(): void {
    if (!this.form.providerName.trim() || !this.form.providerType || !this.form.baseUrl.trim() || !this.form.authType || !this.form.timeoutSec) {
      Swal.fire('Warning', 'Provider name, type, base URL, auth type and timeout are required.', 'warning');
      return;
    }

    this.saving = true;
    this.integrationService.createProvider({
      providerCode: this.form.providerCode?.trim() || null,
      providerName: this.form.providerName.trim(),
      providerType: this.form.providerType,
      baseUrl: this.form.baseUrl.trim(),
      authType: this.form.authType,
      apiKey: this.form.apiKey?.trim() || null,
      username: this.form.username?.trim() || null,
      password: this.form.password?.trim() || null,
      timeoutSec: Number(this.form.timeoutSec)
    }).subscribe({
      next: data => {
        this.saving = false;
        Swal.fire('Success', 'Integration provider created successfully.', 'success');
        this.router.navigate(['/integrations/providers', data.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to create provider.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/integrations/providers']);
  }
}
