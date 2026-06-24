import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import {
  CustomerAddressResponse,
  CustomerIdentityResponse,
  CustomerResponse,
  CustomerTimelineResponse,
  formatEnumLabel
} from '../../models/customer.model';
import { CustomerService } from '../../services/customer.service';
import { KycService } from '../../../kyc/services/kyc.service';

@Component({
  selector: 'app-customer-view',
  templateUrl: './customer-view.component.html',
  styleUrls: ['./customer-view.component.scss']
})
export class CustomerViewComponent implements OnInit {

  id: number | null = null;
  customer: CustomerResponse | null = null;
  addresses: CustomerAddressResponse[] = [];
  identities: CustomerIdentityResponse[] = [];
  timeline: CustomerTimelineResponse[] = [];
  branches: BranchResponse[] = [];
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private customerApi: CustomerService,
    private branchApi: BranchApiService,
    private kycApi: KycService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;

    if (this.id) {
      this.loadCustomer(this.id);
    }
  }

  get primaryAddress(): CustomerAddressResponse | null {
    return this.addresses.find(item => item.primaryAddress) || this.addresses[0] || null;
  }

  get presentAddress(): CustomerAddressResponse | null {
    return this.addresses.find(item => item.addressType === 'PRESENT') || null;
  }

  get permanentAddress(): CustomerAddressResponse | null {
    return this.addresses.find(item => item.addressType === 'PERMANENT') || null;
  }

  get verifiedIdentityCount(): number {
    return this.identities.filter(item => item.verifiedFlag).length;
  }

  get isContactVerified(): boolean {
    if (!this.customer) {
      return false;
    }
    const hasMobile = !!this.customer.mobile;
    const hasEmail = !!this.customer.email;
    const mobileVerified = this.customer.mobileVerified !== false;
    const emailVerified = this.customer.emailVerified !== false;
    return hasMobile && hasEmail && mobileVerified && emailVerified;
  }

  get isKycReady(): boolean {
    return !!this.customer &&
      this.addresses.length > 0 &&
      this.identities.length > 0 &&
      this.verifiedIdentityCount > 0 &&
      !!this.customer.sourceOfFunds;
  }

  get relatedModuleSummary(): string {
    return 'Account, contract, card and transaction navigation is now available from this customer profile.';
  }

  back(): void {
    this.router.navigate(['/customers/list']);
  }

  edit(): void {
    if (!this.customer) return;
    this.router.navigate(['/customers', this.customer.id, 'edit']);
  }

  manageAddress(): void {
    if (!this.customer) return;
    this.router.navigate(['/customers', this.customer.id, 'addresses']);
  }

  manageIdentity(): void {
    if (!this.customer) return;
    this.router.navigate(['/customers', this.customer.id, 'identities']);
  }

  statusAction(): void {
    if (!this.customer) return;
    this.router.navigate(['/customers', this.customer.id, 'status']);
  }

  openKyc(): void {
    if (!this.customer) return;
    this.kycApi.getProfileByCustomerId(this.customer.id).subscribe({
      next: profile => this.router.navigate(['/kyc', profile.id]),
      error: () => this.router.navigate(['/kyc/new'], { queryParams: { customerId: this.customer?.id } })
    });
  }

  openAccountRequest(): void {
    if (!this.customer) return;
    this.router.navigate(['/accounts/opening-requests/new'], { queryParams: { customerId: this.customer.id } });
  }

  openCards(): void {
    if (!this.customer) return;
    this.router.navigate(['/cards/list'], { queryParams: { customerId: this.customer.id } });
  }

  issueCard(): void {
    if (!this.customer) return;
    this.router.navigate(['/cards/new'], { queryParams: { customerId: this.customer.id } });
  }

  openCustomerStatement(): void {
    if (!this.customer) return;
    this.router.navigate(['/statement/customer/request'], { queryParams: { customerId: this.customer.id } });
  }

  openCustomerStatementList(): void {
    if (!this.customer) return;
    this.router.navigate(['/statement/customer/list'], { queryParams: { customerId: this.customer.id } });
  }

  openDepositSchemeList(): void {
    if (!this.customer) return;
    this.router.navigate(['/deposit-schemes/enrollments/list'], { queryParams: { customerId: this.customer.id } });
  }

  openDepositSchemeEnrollment(): void {
    if (!this.customer) return;
    this.router.navigate(['/deposit-schemes/enrollments/new'], { queryParams: { customerId: this.customer.id } });
  }

  openFinancingList(): void {
    if (!this.customer) return;
    this.router.navigate(['/financing/applications'], { queryParams: { customerId: this.customer.id } });
  }

  openFinancingApplication(): void {
    if (!this.customer) return;
    this.router.navigate(['/financing/applications/new'], { queryParams: { customerId: this.customer.id, branchId: this.customer.branchId } });
  }

  openZakatProfiles(): void {
    if (!this.customer) return;
    this.router.navigate(['/zakat/profiles'], { queryParams: { customerId: this.customer.id } });
  }

  runZakat(): void {
    if (!this.customer) return;
    this.router.navigate(['/zakat/calc-run'], { queryParams: { customerId: this.customer.id } });
  }

  openVerificationLogs(): void {
    if (!this.customer) return;
    this.router.navigate(['/verification/logs'], {
      queryParams: {
        keyword: this.customer.customerCode
      }
    });
  }

  sendContactOtp(channelType: 'EMAIL' | 'SMS'): void {
    if (!this.customer) return;
    const contactValue = channelType === 'EMAIL' ? (this.customer.email || '') : (this.customer.mobile || '');
    this.router.navigate(['/verification/provider-test'], {
      queryParams: {
        customerId: this.customer.id,
        channelType,
        contactValue,
        referenceModule: 'CUSTOMER',
        referenceId: this.customer.id
      }
    });
  }

  openContracts(): void {
    if (!this.customer) return;
    this.router.navigate(['/contracts/list'], { queryParams: { customerId: this.customer.id } });
  }

  generateContract(): void {
    if (!this.customer) return;
    this.router.navigate(['/contracts/generate'], {
      queryParams: {
        customerId: this.customer.id,
        referenceModule: 'GENERAL',
        referenceId: this.customer.id
      }
    });
  }

  archive(): void {
    if (!this.customer) return;
    this.customerApi.archive(this.customer.id).subscribe({
      next: () => {
        Swal.fire('Archived', 'Customer archived successfully.', 'success');
        if (this.id) this.loadCustomer(this.id);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Archive failed.', 'error')
    });
  }

  restore(): void {
    if (!this.customer) return;
    this.customerApi.restore(this.customer.id).subscribe({
      next: () => {
        Swal.fire('Restored', 'Customer restored successfully.', 'success');
        if (this.id) this.loadCustomer(this.id);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Restore failed.', 'error')
    });
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) {
      return 'Unassigned Branch';
    }

    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId || '-'}`;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }

  print(): void {
    if (!this.customer) {
      Swal.fire('No data', 'No customer information to print.', 'warning');
      return;
    }

    const c = this.customer;
    const addressRows = this.addresses.length
      ? this.addresses.map(item => `
          <tr>
            <td>${this.safe(item.addressType)}</td>
            <td>${this.safe(item.addressLine1)}</td>
            <td>${this.safe(item.postalCode)}</td>
            <td>${this.safe(item.primaryAddress ? 'YES' : 'NO')}</td>
          </tr>
        `).join('')
      : '<tr><td colspan="4">No address found</td></tr>';

    const identityRows = this.identities.length
      ? this.identities.map(item => `
          <tr>
            <td>${this.safe(item.documentType)}</td>
            <td>${this.safe(item.documentNo)}</td>
            <td>${this.safe(item.issueCountry)}</td>
            <td>${this.safe(item.verifiedFlag ? 'YES' : 'NO')}</td>
          </tr>
        `).join('')
      : '<tr><td colspan="4">No identity found</td></tr>';

    const html = `
      <html>
        <head>
          <title>Customer Profile - ${this.safe(c.customerCode)}</title>
          <style>
            body { font-family: Arial, sans-serif; padding: 18px; color: #0f172a; }
            .print-header { display:flex; justify-content:space-between; border-bottom:2px solid #0d9488; padding-bottom:10px; margin-bottom:14px; }
            h2 { margin:0; font-size:20px; }
            p { margin:4px 0 0; color:#64748b; font-size:12px; }
            .meta { text-align:right; font-size:11px; color:#64748b; }
            .section { margin-top:14px; }
            .section h3 { margin:0 0 8px; color:#0f766e; font-size:14px; }
            table { width:100%; border-collapse:collapse; font-size:11px; }
            th, td { border:1px solid #cbd5e1; padding:7px; text-align:left; }
            th { background:#f1f5f9; color:#334155; width:26%; }
            @page { size:A4; margin:10mm; }
          </style>
        </head>
        <body>
          <div class="print-header">
            <div>
              <h2>Customer Profile</h2>
              <p>Al-Barakah Shariah Banking Management System</p>
            </div>
            <div class="meta">
              Customer Code: ${this.safe(c.customerCode)}<br>
              Printed: ${new Date().toLocaleString()}
            </div>
          </div>

          <div class="section">
            <h3>Customer Profile</h3>
            <table>
              <tr><th>Full Name</th><td>${this.safe(c.fullName)}</td></tr>
              <tr><th>Customer Type</th><td>${this.safe(c.customerType)}</td></tr>
              <tr><th>Branch</th><td>${this.safe(this.getBranchName(c.branchId))}</td></tr>
              <tr><th>Mobile</th><td>${this.safe(c.mobile)}</td></tr>
              <tr><th>Email</th><td>${this.safe(c.email || '-')}</td></tr>
              <tr><th>Nationality</th><td>${this.safe(c.nationality || '-')}</td></tr>
              <tr><th>Status</th><td>${this.safe(c.customerStatus)}</td></tr>
            </table>
          </div>

          <div class="section">
            <h3>Address Summary</h3>
            <table>
              <thead>
                <tr><th>Type</th><th>Address Line 1</th><th>Postal Code</th><th>Primary</th></tr>
              </thead>
              <tbody>${addressRows}</tbody>
            </table>
          </div>

          <div class="section">
            <h3>Identity Summary</h3>
            <table>
              <thead>
                <tr><th>Document Type</th><th>Document No</th><th>Issue Country</th><th>Verified</th></tr>
              </thead>
              <tbody>${identityRows}</tbody>
            </table>
          </div>

          <div class="section">
            <h3>KYC Readiness</h3>
            <table>
              <tr><th>Contact Verification</th><td>${this.safe(this.isContactVerified ? 'READY' : 'PENDING')}</td></tr>
              <tr><th>Verified Identity Count</th><td>${this.safe(this.verifiedIdentityCount)}</td></tr>
              <tr><th>KYC Summary</th><td>${this.safe(this.isKycReady ? 'Ready for KYC processing' : 'Address, identity or verification is still pending')}</td></tr>
            </table>
          </div>
        </body>
      </html>
    `;

    const printWindow = window.open('', '_blank');
    if (!printWindow) {
      Swal.fire('Popup blocked', 'Please allow popup and try again.', 'error');
      return;
    }

    printWindow.document.open();
    printWindow.document.write(html);
    printWindow.document.close();
    printWindow.onload = () => {
      printWindow.focus();
      printWindow.print();
    };
  }

  private loadCustomer(id: number): void {
    this.loading = true;

    forkJoin({
      customer: this.customerApi.getById(id),
      addresses: this.customerApi.getAddressesByCustomer(id).pipe(catchError(() => of([]))),
      identities: this.customerApi.getIdentitiesByCustomer(id).pipe(catchError(() => of([]))),
      timeline: this.customerApi.getTimeline(id).pipe(catchError(() => of([]))),
      branches: this.branchApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ customer, addresses, identities, timeline, branches }) => {
        this.customer = customer;
        this.addresses = addresses;
        this.identities = identities;
        this.timeline = timeline;
        this.branches = branches;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load customer profile.', 'error');
      }
    });
  }

  private safe(value: any): string {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }
}
