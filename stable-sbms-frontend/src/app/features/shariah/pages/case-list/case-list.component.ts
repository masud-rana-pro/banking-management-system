import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { REFERENCE_MODULE_OPTIONS, ShariahReviewCaseRequest, ShariahReviewCaseResponse, formatEnumLabel } from '../../models/shariah.model';
import { ShariahService } from '../../services/shariah.service';

@Component({
  selector: 'app-case-list',
  templateUrl: './case-list.component.html',
  styleUrls: ['./case-list.component.scss']
})
export class CaseListComponent implements OnInit {

  readonly referenceModules = REFERENCE_MODULE_OPTIONS;
  loading = false;
  allItems: ShariahReviewCaseResponse[] = [];
  items: ShariahReviewCaseResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  showCreateForm = false;
  filters = {
    search: '',
    referenceModule: '',
    caseStatus: ''
  };
  form: ShariahReviewCaseRequest = {
    referenceModule: '',
    referenceId: null,
    submittedBy: '',
    remarks: ''
  };

  constructor(
    private shariahService: ShariahService,
    private accessControl: AccessControlService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.form.referenceModule = this.route.snapshot.queryParamMap.get('referenceModule') || '';
    this.form.referenceId = this.toNumber(this.route.snapshot.queryParamMap.get('referenceId'));
    this.showCreateForm = this.route.snapshot.queryParamMap.get('create') === '1';
    this.filters.referenceModule = this.route.snapshot.queryParamMap.get('referenceModule') || '';
    this.load();
  }

  load(): void {
    this.loading = true;
    this.shariahService.getCases().subscribe({
      next: data => {
        this.allItems = data;
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load Shariah review case list.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchKeyword = !keyword
        || item.caseNo.toLowerCase().includes(keyword)
        || item.referenceModule.toLowerCase().includes(keyword)
        || item.submittedBy.toLowerCase().includes(keyword)
        || String(item.referenceId).includes(keyword)
        || String(item.remarks || '').toLowerCase().includes(keyword);
      const matchModule = !this.filters.referenceModule || item.referenceModule === this.filters.referenceModule;
      const matchStatus = !this.filters.caseStatus || item.caseStatus === this.filters.caseStatus;
      return matchKeyword && matchModule && matchStatus;
    });

    this.total = filtered.length;
    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  onSearch(): void {
    this.page = 1;
    this.applyFilters();
  }

  onReset(): void {
    this.filters = { search: '', referenceModule: '', caseStatus: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
  }

  submitCase(): void {
    if (!this.can('SHARIAH_REVIEW_ACCESS')) return;
    if (!this.form.referenceModule || !this.form.referenceId || !this.form.submittedBy.trim()) {
      Swal.fire('Missing data', 'Reference module, reference id and submitted by are required.', 'warning');
      return;
    }

    this.shariahService.createCase(this.form).subscribe({
      next: data => {
        Swal.fire('Success', 'Shariah review case created successfully.', 'success');
        this.showCreateForm = false;
        this.form = { referenceModule: '', referenceId: null, submittedBy: '', remarks: '' };
        this.load();
        this.router.navigate(['/shariah/cases', data.id, 'review']);
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to create Shariah review case.', 'error');
      }
    });
  }

  onView(item: ShariahReviewCaseResponse): void {
    this.router.navigate(['/shariah/cases', item.id]);
  }

  onReview(item: ShariahReviewCaseResponse): void {
    this.router.navigate(['/shariah/cases', item.id, 'review']);
  }

  onHistory(item: ShariahReviewCaseResponse): void {
    this.router.navigate(['/shariah/cases', item.id], { queryParams: { tab: 'history' } });
  }

  async onDecision(item: ShariahReviewCaseResponse, action: 'approve' | 'reject' | 'return'): Promise<void> {
    const permissionCode = action === 'approve'
      ? 'SHARIAH_APPROVE'
      : action === 'reject'
        ? 'SHARIAH_REJECT'
        : 'SHARIAH_RETURN';
    if (!this.can(permissionCode)) return;
    const title = action === 'approve' ? 'Approve Case' : action === 'reject' ? 'Reject Case' : 'Return Case';
    const result = await Swal.fire({
      title,
      html: `
        <input id="decisionBy" class="swal2-input" placeholder="Decision by">
        <textarea id="decisionRemarks" class="swal2-textarea" placeholder="Remarks${action !== 'approve' ? ' (required)' : ''}"></textarea>
      `,
      focusConfirm: false,
      showCancelButton: true,
      preConfirm: () => {
        const decisionBy = (document.getElementById('decisionBy') as HTMLInputElement)?.value?.trim();
        const remarks = (document.getElementById('decisionRemarks') as HTMLTextAreaElement)?.value?.trim();
        if (!decisionBy) {
          Swal.showValidationMessage('Decision by is required.');
          return null;
        }
        if (action !== 'approve' && !remarks) {
          Swal.showValidationMessage('Remarks are required for this action.');
          return null;
        }
        return { decisionBy, remarks: remarks || '', checklistItems: [] };
      }
    });

    if (!result.isConfirmed || !result.value) {
      return;
    }

    const request = result.value;
    const api$ = action === 'approve'
      ? this.shariahService.approve(item.id, request)
      : action === 'reject'
        ? this.shariahService.reject(item.id, request)
        : this.shariahService.returnCase(item.id, request);

    api$.subscribe({
      next: () => {
        Swal.fire('Success', `Case ${action === 'return' ? 'returned' : `${action}d`} successfully.`, 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || `Failed to ${action} case.`, 'error');
      }
    });
  }

  openCorrectionQueue(): void {
    this.router.navigate(['/shariah/correction-queue']);
  }

  openCertificates(): void {
    this.router.navigate(['/shariah/fatwa-certificates']);
  }

  openAnnualReport(): void {
    this.router.navigate(['/shariah/annual-report']);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  getActionTitle(label: string, permissionCode?: string): string {
    if (!permissionCode || this.can(permissionCode)) {
      return label;
    }
    return `${label} (No permission)`;
  }

  get pendingCount(): number {
    return this.allItems.filter(item => item.caseStatus === 'PENDING_REVIEW').length;
  }

  get approvedCount(): number {
    return this.allItems.filter(item => item.caseStatus === 'APPROVED').length;
  }

  get rejectedCount(): number {
    return this.allItems.filter(item => item.caseStatus === 'REJECTED').length;
  }

  get returnedCount(): number {
    return this.allItems.filter(item => item.caseStatus === 'RETURNED').length;
  }

  private toNumber(value: string | null): number | null {
    if (!value) return null;
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
  }
}
