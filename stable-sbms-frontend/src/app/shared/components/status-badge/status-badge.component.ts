import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-status-badge',
  templateUrl: './status-badge.component.html',
  styleUrls: ['./status-badge.component.scss']
})
export class StatusBadgeComponent {
  @Input() status = 'ACTIVE';

  get label(): string {
    const s = (this.status || '').trim();
    // Replace underscores with spaces for display
    return s.replace(/_/g, ' ');
  }

  get cssClass(): string {
    const s = (this.status || '').trim().toUpperCase();
    if (s === 'ACTIVE')                          return 'badge-active';
    if (s === 'DISBURSED' || s === 'PAID' || s === 'CALCULATED' || s === 'DEDUCTED')       return 'badge-approved';
    if (s === 'COMPLETED' || s === 'MATCHED' || s === 'POSTED' || s === 'CLEARED' || s === 'EXECUTED')    return 'badge-approved';
    if (s === 'OVERDUE' || s === 'PARTIAL')      return 'badge-pending';
    if (s === 'LOW_CASH' || s === 'MAINTENANCE' || s === 'OUT_OF_SERVICE') return 'badge-pending';
    if (s === 'PENDING' || s === 'PENDING_REVIEW' || s === 'PENDING_KYC' || s === 'PENDING_ACTIVATION' || s === 'DRAFT' || s === 'SUBMITTED' || s === 'SENT_BACK' || s === 'RECEIVED' || s === 'PAUSED' || s === 'PROFILED' || s === 'SENT') return 'badge-pending';
    if (s === 'LOCKED' || s === 'REJECTED' || s === 'ARCHIVED' || s === 'CANCELLED' || s === 'VARIANCE_FOUND' || s === 'BLOCKED' || s === 'FROZEN' || s === 'CLOSED' || s === 'FAILED' || s === 'RETURNED' || s === 'REVERSED' || s === 'BELOW_NISAB' || s === 'EXPIRED') return 'badge-locked';
    if (s === 'APPROVED' || s === 'VERIFIED' || s === 'ASSET_VERIFIED')     return 'badge-approved';
    if (s === 'FULL')                             return 'badge-review';
    if (s === 'UNDER_REVIEW' || s === 'REVIEW' || s === 'SHARIAH_REVIEW' || s === 'CHECKLIST_UPDATED')   return 'badge-review';
    return 'badge-inactive';
  }

  get iconClass(): string {
    const s = (this.status || '').trim().toUpperCase();
    if (s === 'ACTIVE')                            return 'bi-check-circle-fill';
    if (s === 'DISBURSED' || s === 'PAID' || s === 'CALCULATED' || s === 'DEDUCTED')         return 'bi-patch-check-fill';
    if (s === 'COMPLETED' || s === 'MATCHED' || s === 'POSTED' || s === 'CLEARED' || s === 'EXECUTED')      return 'bi-patch-check-fill';
    if (s === 'OVERDUE' || s === 'PARTIAL')        return 'bi-exclamation-circle-fill';
    if (s === 'LOW_CASH')                          return 'bi-exclamation-circle-fill';
    if (s === 'MAINTENANCE')                       return 'bi-tools';
    if (s === 'OUT_OF_SERVICE')                    return 'bi-slash-circle-fill';
    if (s === 'PENDING' || s === 'PENDING_REVIEW' || s === 'PENDING_KYC' || s === 'PENDING_ACTIVATION' || s === 'DRAFT' || s === 'SUBMITTED' || s === 'RECEIVED' || s === 'PAUSED' || s === 'PROFILED' || s === 'SENT') return 'bi-clock-fill';
    if (s === 'SENT_BACK')                        return 'bi-arrow-return-left';
    if (s === 'LOCKED' || s === 'BLOCKED' || s === 'FROZEN')         return 'bi-lock-fill';
    if (s === 'REJECTED' || s === 'ARCHIVED' || s === 'CANCELLED' || s === 'VARIANCE_FOUND' || s === 'CLOSED' || s === 'FAILED' || s === 'RETURNED' || s === 'REVERSED' || s === 'BELOW_NISAB' || s === 'EXPIRED') return 'bi-x-circle-fill';
    if (s === 'APPROVED' || s === 'VERIFIED' || s === 'ASSET_VERIFIED')      return 'bi-patch-check-fill';
    if (s === 'FULL')                              return 'bi-stack';
    if (s === 'UNDER_REVIEW' || s === 'REVIEW' || s === 'SHARIAH_REVIEW' || s === 'CHECKLIST_UPDATED')    return 'bi-eye-fill';
    return 'bi-dash-circle-fill';
  }
}
