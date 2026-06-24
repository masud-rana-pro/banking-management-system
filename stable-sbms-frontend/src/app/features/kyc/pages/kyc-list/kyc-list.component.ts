import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import {
  KYC_REVIEW_STATUS_OPTIONS,
  RECORD_STATUS_OPTIONS,
  RISK_LEVEL_OPTIONS,
  KycDecisionActionRequest,
  KycProfileResponse,
  formatEnumLabel
} from '../../models/kyc.model';
import { KycService } from '../../services/kyc.service';

@Component({
  selector: 'app-kyc-list',
  templateUrl: './kyc-list.component.html',
  styleUrls: ['./kyc-list.component.scss']
})
export class KycListComponent implements OnInit {

  allProfiles: KycProfileResponse[] = [];
  profiles: KycProfileResponse[] = [];
  filteredProfiles: KycProfileResponse[] = [];
  branches: BranchResponse[] = [];
  customerImageMap: Record<number, string> = {};

  loading = false;
  page = 1;
  pageSize = 10;
  total = 0;

  totalAll = 0;
  pendingCount = 0;
  verifiedCount = 0;
  highRiskCount = 0;

  filters = {
    search: '',
    reviewStatus: '',
    riskLevel: '',
    branchId: ''
  };

  reviewStatusOptions = [{ value: '', label: 'All Review Status' }, ...KYC_REVIEW_STATUS_OPTIONS.map(item => ({ value: item.value, label: item.label }))];
  riskLevelOptions = [{ value: '', label: 'All Risk Levels' }, ...RISK_LEVEL_OPTIONS.map(item => ({ value: item.value, label: item.label }))];
  recordStatusOptions = RECORD_STATUS_OPTIONS;

  constructor(
    private kycApi: KycService,
    private branchApi: BranchApiService,
    private customerApi: CustomerService,
    private router: Router,
    public accessControl: AccessControlService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    if (this.isBranchSelectionLocked) {
      this.filters.branchId = String(this.accessControl.session?.branchId || '');
    }
    this.loadData();
  }

  loadData(): void {
    this.loading = true;

    forkJoin({
      profiles: this.kycApi.getProfiles(),
      branches: this.branchApi.getAll(),
      customers: this.customerApi.getAll()
    }).subscribe({
      next: ({ profiles, branches, customers }) => {
        this.allProfiles = profiles || [];
        this.branches = branches || [];
        this.customerImageMap = this.buildCustomerImageMap(customers || []);
        this.calculateSummary();
        this.applyFiltersAndPaging(true);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load KYC profile list.', 'error');
      }
    });
  }

  onSearch(): void {
    this.applyFiltersAndPaging(true);
  }

  onReset(): void {
    this.filters = {
      search: '',
      reviewStatus: '',
      riskLevel: '',
      branchId: ''
    };
    if (this.isBranchSelectionLocked) {
      this.filters.branchId = String(this.accessControl.session?.branchId || '');
    }
    this.applyFiltersAndPaging(true);
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFiltersAndPaging(false);
  }

  onView(item: KycProfileResponse): void {
    this.router.navigate(['/kyc', item.id]);
  }

  onEdit(item: KycProfileResponse): void {
    this.router.navigate(['/kyc', item.id, 'edit']);
  }

  onReview(item: KycProfileResponse): void {
    this.router.navigate(['/kyc', item.id, 'review']);
  }

  onUpload(item: KycProfileResponse): void {
    this.router.navigate(['/kyc', item.id, 'documents']);
  }

  onHistory(item: KycProfileResponse): void {
    this.router.navigate(['/kyc', item.id, 'history']);
  }

  onOpenCustomer(item: KycProfileResponse): void {
    this.router.navigate(['/customers', item.customerId]);
  }

  onSubmitProfile(item: KycProfileResponse): void {
    this.confirmAndRun('Submit KYC profile?', `${item.customerName} will be submitted for review.`, () => this.kycApi.submitProfile(item.id));
  }

  onVerify(item: KycProfileResponse): void {
    this.confirmAndRun('Verify KYC profile?', `${item.customerName} will be marked as verified.`, () => this.kycApi.verifyProfile(item.id));
  }

  onApprove(item: KycProfileResponse): void {
    this.confirmAndRun('Approve KYC profile?', `${item.customerName} will be approved and customer status can move forward.`, () => this.kycApi.approveProfile(item.id));
  }

  onReject(item: KycProfileResponse): void {
    Swal.fire({
      icon: 'warning',
      title: 'Reject KYC profile?',
      input: 'textarea',
      inputLabel: 'Remarks',
      inputPlaceholder: 'Optional rejection remarks',
      showCancelButton: true,
      confirmButtonText: 'Reject'
    }).then(result => {
      if (!result.isConfirmed) return;
      const payload: KycDecisionActionRequest = { remarks: String(result.value || '').trim() };
      this.kycApi.rejectProfile(item.id, payload).subscribe({
        next: () => {
          Swal.fire('Rejected', 'KYC profile rejected successfully.', 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Reject failed.', 'error')
      });
    });
  }

  onReturn(item: KycProfileResponse): void {
    Swal.fire({
      icon: 'question',
      title: 'Return KYC profile?',
      input: 'textarea',
      inputLabel: 'Correction Remarks',
      inputPlaceholder: 'Write mandatory correction remarks',
      inputValidator: value => value && value.trim() ? null : 'Correction remarks are required',
      showCancelButton: true,
      confirmButtonText: 'Return'
    }).then(result => {
      if (!result.isConfirmed) return;
      const payload: KycDecisionActionRequest = { remarks: String(result.value || '').trim() };
      this.kycApi.returnProfile(item.id, payload).subscribe({
        next: () => {
          Swal.fire('Returned', 'KYC profile returned successfully.', 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Return failed.', 'error')
      });
    });
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) {
      return 'Unassigned Branch';
    }

    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId}`;
  }

  getStatusLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getRiskClass(value?: string | null): string {
    return String(value || '').toLowerCase();
  }

  getImageUrl(customerId?: number | null): string {
    return this.fileUploadService.resolveImageUrl(customerId ? this.customerImageMap[customerId] : '');
  }

  hasCustomerImage(customerId?: number | null): boolean {
    return !!(customerId && this.customerImageMap[customerId]);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  canAny(permissionCodes: string[]): boolean {
    return this.accessControl.hasAnyPermission(permissionCodes);
  }

  get isBranchSelectionLocked(): boolean {
    const roleCode = (this.accessControl.session?.roleCode || '').toUpperCase();
    const globalRoles = ['SYSTEM_ADMIN', 'MIS_OFFICER', 'COMPLIANCE_OFFICER', 'INTERNAL_AUDITOR', 'TREASURY_FINANCE_OFFICER'];
    return !!this.accessControl.session?.branchId && !globalRoles.includes(roleCode);
  }

  onPrint(): void {
    const data = this.getExportableData();

    if (!data.length) {
      Swal.fire('No data', 'No KYC data to print.', 'warning');
      return;
    }

    const rows = data.map((item, index) => `
      <tr>
        <td>${index + 1}</td>
        <td>${this.safe(item.customerCode)}</td>
        <td>${this.safe(item.customerName)}</td>
        <td>${this.safe(item.riskLevel)}</td>
        <td>${this.safe(item.reviewStatus)}</td>
        <td>${this.safe(this.getBranchName(item.branchId))}</td>
        <td>${this.safe(item.documentCount || 0)}</td>
        <td>${this.safe(item.reviewedBy || '-')}</td>
      </tr>
    `).join('');

    const html = `
      <html>
        <head>
          <title>KYC Profile List</title>
          <style>
            body { font-family: Arial, sans-serif; padding: 18px; color: #0f172a; }
            .print-header { display:flex; justify-content:space-between; border-bottom:2px solid #0d9488; padding-bottom:10px; margin-bottom:14px; }
            h2 { margin:0; font-size:20px; }
            p { margin:4px 0 0; color:#64748b; font-size:12px; }
            .meta { text-align:right; font-size:11px; color:#64748b; }
            table { width:100%; border-collapse:collapse; table-layout:fixed; font-size:10.5px; }
            th, td { border:1px solid #cbd5e1; padding:6px 7px; text-align:left; word-wrap:break-word; }
            th { background:#f1f5f9; color:#334155; font-weight:700; }
            @page { size:A4 landscape; margin:8mm; }
          </style>
        </head>
        <body>
          <div class="print-header">
            <div>
              <h2>KYC Profile List</h2>
              <p>Al-Barakah Shariah Banking Management System</p>
            </div>
            <div class="meta">
              Total Records: ${data.length}<br>
              Printed: ${new Date().toLocaleString()}
            </div>
          </div>
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Customer Code</th>
                <th>Customer</th>
                <th>Risk</th>
                <th>Review Status</th>
                <th>Branch</th>
                <th>Documents</th>
                <th>Reviewer</th>
              </tr>
            </thead>
            <tbody>${rows}</tbody>
          </table>
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

  onExport(type: 'csv' | 'excel' | 'pdf'): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No KYC data to export.', 'warning');
      return;
    }

    if (type === 'csv') this.exportCSV(data);
    if (type === 'excel') this.exportExcel(data);
    if (type === 'pdf') this.exportPDF(data);
  }

  private applyFiltersAndPaging(resetPage: boolean): void {
    if (resetPage) this.page = 1;

    const search = this.filters.search.trim().toLowerCase();
    const reviewStatus = this.filters.reviewStatus.trim();
    const riskLevel = this.filters.riskLevel.trim();
    const branchId = this.filters.branchId.trim();

    const filtered = this.allProfiles.filter(item => {
      const matchSearch =
        !search ||
        (item.customerCode || '').toLowerCase().includes(search) ||
        (item.customerName || '').toLowerCase().includes(search) ||
        (item.reviewedBy || '').toLowerCase().includes(search);

      const matchReviewStatus = !reviewStatus || item.reviewStatus === reviewStatus;
      const matchRiskLevel = !riskLevel || item.riskLevel === riskLevel;
      const matchBranch = !branchId || String(item.branchId || '') === branchId;

      return matchSearch && matchReviewStatus && matchRiskLevel && matchBranch;
    });

    this.filteredProfiles = filtered;
    this.total = filtered.length;

    const start = (this.page - 1) * this.pageSize;
    this.profiles = filtered.slice(start, start + this.pageSize);
  }

  private calculateSummary(): void {
    this.totalAll = this.allProfiles.length;
    this.pendingCount = this.allProfiles.filter(item => ['DRAFT', 'SUBMITTED', 'UNDER_REVIEW'].includes(item.reviewStatus)).length;
    this.verifiedCount = this.allProfiles.filter(item => ['VERIFIED', 'APPROVED'].includes(item.reviewStatus)).length;
    this.highRiskCount = this.allProfiles.filter(item => item.riskLevel === 'HIGH').length;
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, item) => {
      acc[item.id] = item.profileImageName || '';
      return acc;
    }, {} as Record<number, string>);
  }

  private confirmAndRun(title: string, text: string, request: () => any): void {
    Swal.fire({
      icon: 'question',
      title,
      text,
      showCancelButton: true,
      confirmButtonText: 'Continue'
    }).then(result => {
      if (!result.isConfirmed) return;

      request().subscribe({
        next: () => {
          Swal.fire('Success', 'KYC workflow action completed successfully.', 'success');
          this.loadData();
        },
        error: (err: any) => Swal.fire('Error', err?.error?.message || 'KYC workflow action failed.', 'error')
      });
    });
  }

  private getExportableData(): KycProfileResponse[] {
    const hasFilter = Object.values(this.filters).some(item => !!item.trim());
    return hasFilter ? this.filteredProfiles : this.allProfiles;
  }

  private safe(value: any): string {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  private exportCSV(data: KycProfileResponse[]): void {
    const headers = ['Customer Code', 'Customer', 'Risk Level', 'Review Status', 'Branch', 'Document Count', 'Reviewer'];
    const rows = data.map(item => [
      item.customerCode,
      item.customerName,
      item.riskLevel || '',
      item.reviewStatus,
      this.getBranchName(item.branchId),
      String(item.documentCount || 0),
      item.reviewedBy || ''
    ]);

    const csv = [headers, ...rows].map(row => row.map(value => `"${value}"`).join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'kyc-profile-list.csv';
    link.click();
  }

  private exportExcel(data: KycProfileResponse[]): void {
    import('xlsx').then(xlsx => {
      const rows = data.map(item => ({
        'Customer Code': item.customerCode,
        Customer: item.customerName,
        'Risk Level': item.riskLevel || '',
        'Review Status': item.reviewStatus,
        Branch: this.getBranchName(item.branchId),
        Documents: item.documentCount || 0,
        Reviewer: item.reviewedBy || ''
      }));

      const worksheet = xlsx.utils.json_to_sheet(rows);
      const workbook = { Sheets: { KycProfiles: worksheet }, SheetNames: ['KycProfiles'] };
      const buffer = xlsx.write(workbook, { bookType: 'xlsx', type: 'array' });

      const blob = new Blob([buffer], { type: 'application/octet-stream' });
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = 'kyc-profile-list.xlsx';
      link.click();
    });
  }

  private exportPDF(data: KycProfileResponse[]): void {
    import('jspdf').then(jsPDF => {
      import('jspdf-autotable').then(() => {
        const doc = new jsPDF.jsPDF('landscape');
        doc.text('KYC Profile List', 14, 14);

        (doc as any).autoTable({
          head: [['Customer Code', 'Customer', 'Risk Level', 'Review Status', 'Branch', 'Documents', 'Reviewer']],
          body: data.map(item => [
            item.customerCode,
            item.customerName,
            item.riskLevel || '',
            item.reviewStatus,
            this.getBranchName(item.branchId),
            item.documentCount || 0,
            item.reviewedBy || ''
          ]),
          startY: 20,
          styles: { fontSize: 8 }
        });

        doc.save('kyc-profile-list.pdf');
      });
    });
  }
}
