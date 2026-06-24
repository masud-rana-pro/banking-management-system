import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent {
  @Input() title       = 'Confirm Action';
  @Input() message     = 'Are you sure you want to proceed?';
  @Input() confirmText = 'Confirm';
  @Input() cancelText  = 'Cancel';
  @Input() danger      = false;

  @Output() confirm = new EventEmitter<void>();
  @Output() cancel  = new EventEmitter<void>();
}
