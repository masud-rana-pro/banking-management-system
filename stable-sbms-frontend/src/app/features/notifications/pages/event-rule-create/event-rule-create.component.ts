import { Component } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { NotificationEventRequest } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-event-rule-create',
  templateUrl: './event-rule-create.component.html',
  styleUrls: ['./event-rule-create.component.scss']
})
export class EventRuleCreateComponent {

  saving = false;
  form: NotificationEventRequest = {
    eventCode: '',
    eventName: '',
    referenceModule: ''
  };

  constructor(
    private notificationService: NotificationService,
    private router: Router
  ) {}

  submit(): void {
    if (!this.form.eventName?.trim()) {
      Swal.fire('Warning', 'Event name is required.', 'warning');
      return;
    }

    this.saving = true;
    this.notificationService.createEventRule({
      eventCode: this.form.eventCode?.trim() || null,
      eventName: this.form.eventName.trim(),
      referenceModule: this.form.referenceModule?.trim() || null
    }).subscribe({
      next: () => {
        this.saving = false;
        Swal.fire('Success', 'Notification event rule created successfully.', 'success');
        this.router.navigate(['/notifications/event-rules']);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to create event rule.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/notifications/event-rules']);
  }
}
