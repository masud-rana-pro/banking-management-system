import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { KycDecisionHistoryResponse, KycDocumentResponse, KycProfileResponse, formatEnumLabel } from '../../models/kyc.model';
import { KycService } from '../../services/kyc.service';

@Component({
  selector: 'app-kyc-view',
  templateUrl: './kyc-view.component.html',
  styleUrls: ['./kyc-view.component.scss']
})
export class KycViewComponent implements OnInit {

  id: number | null = null;
  profile: KycProfileResponse | null = null;
  documents: KycDocumentResponse[] = [];
  history: KycDecisionHistoryResponse[] = [];
  branches: BranchResponse[] = [];
  loading = false;
  customerProfileImageName = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private kycApi: KycService,
    private branchApi: BranchApiService,
    private customerApi: CustomerService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;

    if (this.id) {
      this.loadData(this.id);
    }
  }

  back(): void {
    this.router.navigate(['/kyc/list']);
  }

  edit(): void {
    if (!this.profile) return;
    this.router.navigate(['/kyc', this.profile.id, 'edit']);
  }

  review(): void {
    if (!this.profile) return;
    this.router.navigate(['/kyc', this.profile.id, 'review']);
  }

  upload(): void {
    if (!this.profile) return;
    this.router.navigate(['/kyc', this.profile.id, 'documents']);
  }

  openHistory(): void {
    if (!this.profile) return;
    this.router.navigate(['/kyc', this.profile.id, 'history']);
  }

  openCustomer(): void {
    if (!this.profile) return;
    this.router.navigate(['/customers', this.profile.customerId]);
  }

  openAccountRequest(): void {
    if (!this.profile) return;
    this.router.navigate(['/accounts/opening-requests/new'], { queryParams: { customerId: this.profile.customerId } });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getRiskClass(value?: string | null): string {
    return String(value || '').toLowerCase();
  }

  getCustomerImageUrl(): string {
    return this.fileUploadService.resolveImageUrl(this.customerProfileImageName);
  }

  getDocumentImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveDocumentUrl(fileName);
  }

  isImageFile(fileName?: string | null): boolean {
    return this.fileUploadService.isImageFile(fileName);
  }

  isPdfFile(fileName?: string | null): boolean {
    return this.fileUploadService.isPdfFile(fileName);
  }

  openDocument(fileName?: string | null): void {
    const url = this.getDocumentImageUrl(fileName);
    if (url) {
      window.open(url, '_blank');
    }
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) {
      return 'Unassigned Branch';
    }

    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId}`;
  }

  print(): void {
    if (!this.profile) {
      Swal.fire('No data', 'No KYC profile to print.', 'warning');
      return;
    }

    const p = this.profile;
    const documentRows = this.documents.length
      ? this.documents.map(item => `
          <tr>
            <td>${this.safe(item.documentType)}</td>
            <td>${this.safe(item.documentNo)}</td>
            <td>${this.safe(item.fileReferenceId || '-')}</td>
            <td>${this.safe(item.verifiedFlag ? 'YES' : 'NO')}</td>
          </tr>
        `).join('')
      : '<tr><td colspan="4">No document found</td></tr>';

    const historyRows = this.history.length
      ? this.history.map(item => `
          <tr>
            <td>${this.safe(item.decision)}</td>
            <td>${this.safe(item.decisionBy)}</td>
            <td>${this.safe(item.decisionAt || '')}</td>
            <td>${this.safe(item.remarks || '-')}</td>
          </tr>
        `).join('')
      : '<tr><td colspan="4">No decision history found</td></tr>';

    const html = `
      <html>
        <head>
          <title>KYC Profile - ${this.safe(p.customerCode)}</title>
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
              <h2>KYC Profile</h2>
              <p>Al-Barakah Shariah Banking Management System</p>
            </div>
            <div class="meta">
              Customer Code: ${this.safe(p.customerCode)}<br>
              Printed: ${new Date().toLocaleString()}
            </div>
          </div>
          <div class="section">
            <h3>Customer + KYC Summary</h3>
            <table>
              <tr><th>Customer</th><td>${this.safe(p.customerName)}</td></tr>
              <tr><th>Branch</th><td>${this.safe(this.getBranchName(p.branchId))}</td></tr>
              <tr><th>Risk Level</th><td>${this.safe(p.riskLevel)}</td></tr>
              <tr><th>Review Status</th><td>${this.safe(p.reviewStatus)}</td></tr>
              <tr><th>Reviewer</th><td>${this.safe(p.reviewedBy || '-')}</td></tr>
              <tr><th>Reviewed At</th><td>${this.safe(p.reviewedAt || '-')}</td></tr>
              <tr><th>Remarks</th><td>${this.safe(p.remarks || '-')}</td></tr>
            </table>
          </div>
          <div class="section">
            <h3>Documents</h3>
            <table>
              <thead>
                <tr><th>Document Type</th><th>Document No</th><th>File Reference</th><th>Verified</th></tr>
              </thead>
              <tbody>${documentRows}</tbody>
            </table>
          </div>
          <div class="section">
            <h3>Decision History</h3>
            <table>
              <thead>
                <tr><th>Decision</th><th>Decision By</th><th>Decision At</th><th>Remarks</th></tr>
              </thead>
              <tbody>${historyRows}</tbody>
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

  private loadData(id: number): void {
    this.loading = true;

    forkJoin({
      profile: this.kycApi.getProfileById(id),
      documents: this.kycApi.getDocuments(id).pipe(catchError(() => of([]))),
      history: this.kycApi.getDecisionHistory(id).pipe(catchError(() => of([]))),
      branches: this.branchApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ profile, documents, history, branches }) => {
        this.profile = profile;
        this.documents = documents;
        this.history = history;
        this.branches = branches;
        if (!profile.customerId) {
          this.customerProfileImageName = '';
          this.loading = false;
          return;
        }
        this.customerApi.getById(profile.customerId).pipe(catchError(() => of(null as CustomerResponse | null))).subscribe({
          next: customer => {
            this.customerProfileImageName = customer?.profileImageName || '';
            this.loading = false;
          },
          error: () => {
            this.customerProfileImageName = '';
            this.loading = false;
          }
        });
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load KYC profile.', 'error');
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
