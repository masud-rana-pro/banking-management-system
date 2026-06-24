import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { ProviderTestRequest, VerificationLogResponse, formatEnumLabel } from '../../models/verification.model';
import { VerificationService } from '../../services/verification.service';

@Component({
  selector: 'app-provider-test-console',
  templateUrl: './provider-test-console.component.html',
  styleUrls: ['./provider-test-console.component.scss']
})
export class ProviderTestConsoleComponent implements OnInit {

  loading = false;
  result: VerificationLogResponse | null = null;
  form: ProviderTestRequest = {
    channelType: 'EMAIL',
    purpose: 'PROVIDER_TEST',
    contactValue: '',
    referenceModule: '',
    referenceId: null,
    remarks: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private verificationService: VerificationService
  ) {}

  ngOnInit(): void {
    this.form.channelType = (this.route.snapshot.queryParamMap.get('channelType') as any) || 'EMAIL';
    this.form.contactValue = this.route.snapshot.queryParamMap.get('contactValue') || '';
    this.form.referenceModule = this.route.snapshot.queryParamMap.get('referenceModule') || '';
    const referenceId = this.route.snapshot.queryParamMap.get('referenceId');
    this.form.referenceId = referenceId ? Number(referenceId) : null;
    const customerId = this.route.snapshot.queryParamMap.get('customerId');
    if (customerId && !this.form.referenceId) {
      this.form.referenceId = Number(customerId);
      this.form.referenceModule = this.form.referenceModule || 'CUSTOMER';
    }
  }

  submit(): void {
    if (!this.form.contactValue.trim()) {
      Swal.fire('Warning', 'Contact value is required.', 'warning');
      return;
    }
    this.loading = true;
    this.verificationService.providerTest(this.form).subscribe({
      next: data => {
        this.result = data;
        this.loading = false;
        Swal.fire('Sent', 'Provider test OTP sent successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', err?.error?.message || 'Failed to send provider test OTP.', 'error');
      }
    });
  }

  openVerify(): void {
    this.router.navigate(['/verification/otp-verify'], {
      queryParams: this.result ? { requestId: this.result.id } : {}
    });
  }

  openIntegrations(): void {
    this.router.navigate(['/integrations/provider-test'], {
      queryParams: {
        providerId: this.form.channelType === 'SMS' ? 2 : 1
      }
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
