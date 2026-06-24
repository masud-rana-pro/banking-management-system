import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { IntegrationProviderRequest } from '../../models/integration.model';
import { IntegrationService } from '../../services/integration.service';

@Component({
  selector: 'app-provider-edit',
  templateUrl: './provider-edit.component.html',
  styleUrls: ['./provider-edit.component.scss']
})
export class ProviderEditComponent implements OnInit {

  id: number | null = null;
  loading = false;
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
    private route: ActivatedRoute,
    private router: Router,
    private integrationService: IntegrationService
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
    this.integrationService.getProviderById(id).subscribe({
      next: data => {
        this.form = {
          providerCode: data.providerCode,
          providerName: data.providerName,
          providerType: data.providerType,
          baseUrl: data.baseUrl,
          authType: data.authType,
          apiKey: '',
          username: data.username || '',
          password: '',
          timeoutSec: data.timeoutSec
        };
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load provider.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.id) return;
    if (!this.form.providerName.trim() || !this.form.providerType || !this.form.baseUrl.trim() || !this.form.authType || !this.form.timeoutSec) {
      Swal.fire('Warning', 'Provider name, type, base URL, auth type and timeout are required.', 'warning');
      return;
    }

    this.saving = true;
    this.integrationService.updateProvider(this.id, {
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
        Swal.fire('Success', 'Integration provider updated successfully.', 'success');
        this.router.navigate(['/integrations/providers', data.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to update provider.', 'error');
      }
    });
  }

  back(): void {
    if (this.id) {
      this.router.navigate(['/integrations/providers', this.id]);
      return;
    }
    this.router.navigate(['/integrations/providers']);
  }
}
