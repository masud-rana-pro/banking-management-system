import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserHistoryEntry, UserResponse } from '../model/user.model';
import { UserApiService } from '../service/user-api.service';

@Component({
  selector: 'app-user-view',
  templateUrl: './user-view.component.html',
  styleUrls: ['./user-view.component.scss']
})
export class UserViewComponent implements OnInit {
  user?: UserResponse;
  history: UserHistoryEntry[] = [];
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private api: UserApiService,
    public access: AccessControlService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) return;
    this.loading = true;
    this.api.getById(id).subscribe({
      next: data => {
        this.user = data;
        this.api.getHistory(id).subscribe({
          next: history => {
            this.history = history || [];
            this.loading = false;
          },
          error: err => {
            console.error(err);
            this.loading = false;
            Swal.fire('Error', 'Failed to load user history.', 'error');
          }
        });
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load user detail.', 'error');
      }
    });
  }

  onBack(): void { this.router.navigate(['/admin/users']); }
  onEdit(): void { if (this.user) this.router.navigate(['/admin/users', this.user.id, 'edit']); }
  onAssignRole(): void { if (this.user) this.router.navigate(['/admin/users', this.user.id, 'assign-role']); }
  onResetPassword(): void { if (this.user) this.router.navigate(['/admin/users', this.user.id, 'reset-password']); }
  onLockUnlock(): void { if (this.user) this.router.navigate(['/admin/users', this.user.id, 'lock-unlock']); }

  getImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }
}
