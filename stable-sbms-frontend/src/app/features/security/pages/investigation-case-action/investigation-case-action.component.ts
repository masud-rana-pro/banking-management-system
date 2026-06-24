import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { InvestigationCaseActionRequest, InvestigationCaseResponse, formatEnumLabel } from '../../models/security.model';
import { SecurityService } from '../../services/security.service';

@Component({
  selector: 'app-investigation-case-action',
  templateUrl: './investigation-case-action.component.html',
  styleUrls: ['./investigation-case-action.component.scss']
})
export class InvestigationCaseActionComponent implements OnInit {

  loading = false;
  saving = false;
  item: InvestigationCaseResponse | null = null;
  userImageMap: Record<string, string> = {};
  form: InvestigationCaseActionRequest = {
    assignedTo: null,
    remarks: '',
    performedBy: 'SYSTEM',
    evidenceFileName: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private securityService: SecurityService,
    private fileUploadService: FileUploadService,
    private userApi: UserApiService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) this.load(id);
    });
  }

  load(id: number): void {
    this.loading = true;
    this.securityService.getInvestigationCaseById(id).subscribe({
      next: data => {
        this.item = data;
        this.form.assignedTo = data.assignedTo || null;
        this.form.evidenceFileName = data.evidenceFileName || '';
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load investigation case action page.', 'error');
      }
    });
  }

  assignCase(): void {
    if (!this.item) return;
    this.saving = true;
    this.securityService.assignInvestigationCase(this.item.id, this.form).subscribe({
      next: () => {
        this.saving = false;
        Swal.fire('Success', 'Investigation case assigned successfully.', 'success');
        this.router.navigate(['/security/investigation-cases', this.item!.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to assign investigation case.', 'error');
      }
    });
  }

  closeCase(): void {
    if (!this.item) return;
    this.saving = true;
    this.securityService.closeInvestigationCase(this.item.id, this.form).subscribe({
      next: () => {
        this.saving = false;
        Swal.fire('Success', 'Investigation case closed successfully.', 'success');
        this.router.navigate(['/security/investigation-cases', this.item!.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to close investigation case.', 'error');
      }
    });
  }

  openView(): void {
    if (this.item) {
      this.router.navigate(['/security/investigation-cases', this.item.id]);
    }
  }

  onEvidenceSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.fileUploadService.uploadDocument(file).subscribe({
      next: result => {
        this.form.evidenceFileName = result.fileName;
        Swal.fire('Uploaded', 'Evidence file uploaded successfully.', 'success');
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to upload evidence file.', 'error');
      }
    });
  }

  previewEvidence(): void {
    if (!this.form.evidenceFileName) return;
    window.open(this.fileUploadService.resolveDocumentUrl(this.form.evidenceFileName), '_blank');
  }

  hasEvidence(): boolean {
    return !!this.form.evidenceFileName;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getUserImageUrl(username?: string | null): string {
    const key = (username || '').trim().toLowerCase();
    return key ? this.userImageMap[key] || '' : '';
  }

  private loadUsers(): void {
    this.userApi.getAll().subscribe({
      next: users => {
        this.userImageMap = this.buildUserImageMap(users || []);
      },
      error: () => {
        this.userImageMap = {};
      }
    });
  }

  private buildUserImageMap(users: UserResponse[]): Record<string, string> {
    return users.reduce<Record<string, string>>((acc, user) => {
      if (user.username && user.profileImageName) {
        acc[user.username.trim().toLowerCase()] = this.fileUploadService.resolveImageUrl(user.profileImageName);
      }
      return acc;
    }, {});
  }
}
