import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { AtmTerminalService } from '../../services/atm-terminal.service';
import { ReconciliationResponse } from '../../models/terminal.model';

@Component({
  selector: 'app-reconciliation-view',
  templateUrl: './reconciliation-view.component.html',
  styleUrls: ['./reconciliation-view.component.scss']
})
export class ReconciliationViewComponent implements OnInit {

  reconciliation: ReconciliationResponse | null = null;
  loading = false;
  userImageMap: Record<string, string> = {};
  userDisplayMap: Record<number, string> = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private atmApi: AtmTerminalService,
    private userApi: UserApiService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.load(id);
    }
  }

  load(id: number): void {
    this.loading = true;

    this.atmApi.getReconciliationById(id).subscribe({
      next: data => {
        this.reconciliation = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load reconciliation profile.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/atm/reconciliations']);
  }

  viewTerminal(): void {
    if (!this.reconciliation) return;
    this.router.navigate(['/atm/terminals', this.reconciliation.terminalId]);
  }

  getUserImageUrl(userId?: number | null): string {
    if (!userId) return '';
    return this.userImageMap[String(userId)] || '';
  }

  getUserDisplay(userId?: number | null): string {
    if (!userId) return '-';
    return this.userDisplayMap[userId] || `USER-${userId}`;
  }

  print(): void {
    if (!this.reconciliation?.id) {
      Swal.fire('No data', 'No reconciliation data to print.', 'warning');
      return;
    }
    window.open(this.atmApi.getReconciliationReportPreviewUrl(this.reconciliation.id), '_blank');
  }

  previewReport(): void {
    if (!this.reconciliation?.id) return;
    window.open(this.atmApi.getReconciliationReportPreviewUrl(this.reconciliation.id), '_blank');
  }

  downloadReport(): void {
    if (!this.reconciliation?.id) return;
    window.open(this.atmApi.getReconciliationReportDownloadUrl(this.reconciliation.id), '_blank');
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
      if (user.id && user.profileImageName) {
        acc[String(user.id)] = this.fileUploadService.resolveImageUrl(user.profileImageName);
      }
      if (user.id) {
        this.userDisplayMap[user.id] = user.fullName || user.username;
      }
      return acc;
    }, {});
  }
}
