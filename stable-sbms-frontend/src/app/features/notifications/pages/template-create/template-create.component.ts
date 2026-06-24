import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { NotificationTemplateRequest } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-template-create',
  templateUrl: './template-create.component.html',
  styleUrls: ['./template-create.component.scss']
})
export class TemplateCreateComponent implements OnInit {

  id: number | null = null;
  saving = false;
  loading = false;
  form: NotificationTemplateRequest = {
    templateCode: '',
    templateName: '',
    channelType: '',
    subjectText: '',
    bodyText: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    if (this.id) {
      this.load(this.id);
    }
  }

  get isEdit(): boolean {
    return !!this.id;
  }

  load(id: number): void {
    this.loading = true;
    this.notificationService.getTemplateById(id).subscribe({
      next: data => {
        this.form = {
          templateCode: data.templateCode,
          templateName: data.templateName,
          channelType: data.channelType,
          subjectText: data.subjectText || '',
          bodyText: data.bodyText
        };
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load notification template.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.form.templateName.trim() || !this.form.channelType || !this.form.bodyText.trim()) {
      Swal.fire('Warning', 'Template name, channel type and body text are required.', 'warning');
      return;
    }

    this.saving = true;
    const payload: NotificationTemplateRequest = {
      templateCode: this.form.templateCode?.trim() || null,
      templateName: this.form.templateName.trim(),
      channelType: this.form.channelType,
      subjectText: this.form.subjectText?.trim() || null,
      bodyText: this.form.bodyText.trim()
    };

    const request$ = this.isEdit && this.id
      ? this.notificationService.updateTemplate(this.id, payload)
      : this.notificationService.createTemplate(payload);

    request$.subscribe({
      next: data => {
        this.saving = false;
        Swal.fire('Success', `Notification template ${this.isEdit ? 'updated' : 'created'} successfully.`, 'success');
        this.router.navigate(['/notifications/templates', data.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save notification template.', 'error');
      }
    });
  }

  back(): void {
    if (this.id) {
      this.router.navigate(['/notifications/templates', this.id]);
      return;
    }
    this.router.navigate(['/notifications/templates']);
  }
}
