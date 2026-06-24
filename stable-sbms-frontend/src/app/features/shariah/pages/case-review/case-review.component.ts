import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import {
  ShariahChecklistItemResponse,
  ShariahChecklistSelectionRequest,
  ShariahReviewCaseResponse,
  formatEnumLabel
} from '../../models/shariah.model';
import { ShariahService } from '../../services/shariah.service';

@Component({
  selector: 'app-case-review',
  templateUrl: './case-review.component.html',
  styleUrls: ['./case-review.component.scss']
})
export class CaseReviewComponent implements OnInit {

  id = 0;
  loading = false;
  item: ShariahReviewCaseResponse | null = null;
  checklistItems: ShariahChecklistItemResponse[] = [];
  form = {
    reviewedBy: '',
    remarks: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private shariahService: ShariahService,
    private accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load(): void {
    this.loading = true;
    this.shariahService.getCaseById(this.id).subscribe({
      next: data => {
        this.item = data;
        this.checklistItems = (data.checklistItems || []).map(item => ({ ...item }));
        this.form.remarks = data.remarks || '';
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load Shariah review case.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/shariah/cases', this.id]);
  }

  saveChecklist(): void {
    if (!this.can('SHARIAH_CHECKLIST_SAVE')) return;
    this.shariahService.saveChecklist(this.id, {
      reviewedBy: this.form.reviewedBy,
      remarks: this.form.remarks,
      checklistItems: this.buildChecklistRequest()
    }).subscribe({
      next: data => {
        this.item = data;
        this.checklistItems = (data.checklistItems || []).map(item => ({ ...item }));
        Swal.fire('Success', 'Shariah checklist saved successfully.', 'success');
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to save Shariah checklist.', 'error');
      }
    });
  }

  approve(): void {
    if (!this.can('SHARIAH_APPROVE')) return;
    this.runDecision('approve');
  }

  reject(): void {
    if (!this.can('SHARIAH_REJECT')) return;
    this.runDecision('reject');
  }

  returnCase(): void {
    if (!this.can('SHARIAH_RETURN')) return;
    this.runDecision('return');
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  getActionTitle(label: string, permissionCode: string): string {
    return this.can(permissionCode) ? label : `${label} (No permission)`;
  }

  get checklistLocked(): boolean {
    return !this.can('SHARIAH_CHECKLIST_SAVE');
  }

  get remarksLocked(): boolean {
    return !this.accessControl.hasAnyPermission([
      'SHARIAH_CHECKLIST_SAVE',
      'SHARIAH_APPROVE',
      'SHARIAH_REJECT',
      'SHARIAH_RETURN'
    ]);
  }

  private runDecision(action: 'approve' | 'reject' | 'return'): void {
    if (!this.form.reviewedBy.trim()) {
      Swal.fire('Missing reviewer', 'Reviewer / decision by is required.', 'warning');
      return;
    }
    if (action !== 'approve' && !this.form.remarks.trim()) {
      Swal.fire('Missing remarks', 'Remarks are required for reject or return action.', 'warning');
      return;
    }

    const request = {
      decisionBy: this.form.reviewedBy,
      remarks: this.form.remarks,
      checklistItems: this.buildChecklistRequest()
    };

    const api$ = action === 'approve'
      ? this.shariahService.approve(this.id, request)
      : action === 'reject'
        ? this.shariahService.reject(this.id, request)
        : this.shariahService.returnCase(this.id, request);

    api$.subscribe({
      next: data => {
        this.item = data;
        Swal.fire('Success', `Case ${action}d successfully.`, 'success');
        this.router.navigate(['/shariah/cases', this.id]);
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || `Failed to ${action} case.`, 'error');
      }
    });
  }

  private buildChecklistRequest(): ShariahChecklistSelectionRequest[] {
    return this.checklistItems.map(item => ({
      itemId: item.id,
      selected: !!item.selected,
      note: item.note || ''
    }));
  }
}
