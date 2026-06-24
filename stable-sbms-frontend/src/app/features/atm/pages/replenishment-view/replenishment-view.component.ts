import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { AtmTerminalService } from '../../services/atm-terminal.service';
import { ReplenishmentResponse } from '../../models/terminal.model';

@Component({
  selector: 'app-replenishment-view',
  templateUrl: './replenishment-view.component.html',
  styleUrls: ['./replenishment-view.component.scss']
})
export class ReplenishmentViewComponent implements OnInit {

  replenishment: ReplenishmentResponse | null = null;
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

    this.atmApi.getReplenishmentById(id).subscribe({
      next: data => {
        this.replenishment = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load replenishment profile.', 'error');
      }
    });
  }

  previewReport(): void {
    if (!this.replenishment?.id) return;
    window.open(this.atmApi.getReplenishmentReportPreviewUrl(this.replenishment.id), '_blank', 'noopener');
  }

  downloadReport(): void {
    if (!this.replenishment?.id) return;
    window.open(this.atmApi.getReplenishmentReportDownloadUrl(this.replenishment.id), '_blank', 'noopener');
  }

  printReport(): void {
    this.previewReport();
  }

  back(): void {
    this.router.navigate(['/atm/replenishments']);
  }

  viewTerminal(): void {
    if (!this.replenishment) return;
    this.router.navigate(['/atm/terminals', this.replenishment.terminalId]);
  }

  getUserImageUrl(userId?: number | null): string {
    if (!userId) {
      return '';
    }
    return this.userImageMap[String(userId)] || '';
  }

  getUserDisplay(userId?: number | null): string {
    if (!userId) {
      return '-';
    }
    return this.userDisplayMap[userId] || `USER-${userId}`;
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
      if (user.id) this.userDisplayMap[user.id] = user.fullName || user.username;
      return acc;
    }, {});
  }
}
