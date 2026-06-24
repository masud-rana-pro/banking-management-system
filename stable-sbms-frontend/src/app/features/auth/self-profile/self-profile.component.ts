import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { LiveUpdateService } from 'src/app/core/services/live-update.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';

@Component({
  selector: 'app-self-profile',
  templateUrl: './self-profile.component.html',
  styleUrls: ['./self-profile.component.scss']
})
export class SelfProfileComponent implements OnInit, OnDestroy {
  loading = true;
  user: UserResponse | null = null;
  avatarUrl = '';
  isOnline = false;
  private presenceSubscription?: Subscription;

  constructor(
    private accessControl: AccessControlService,
    private userApiService: UserApiService,
    private fileUploadService: FileUploadService,
    private router: Router,
    private liveUpdateService: LiveUpdateService
  ) {}

  ngOnInit(): void {
    const session = this.accessControl.session;
    if (!session?.userId) {
      this.loading = false;
      this.router.navigate(['/auth/login']);
      return;
    }

    this.isOnline = this.liveUpdateService.isUserOnline(session.username);
    this.presenceSubscription = this.liveUpdateService.onlineUsers$.subscribe(() => {
      this.isOnline = this.liveUpdateService.isUserOnline(this.user?.username || session.username);
    });

    this.userApiService.getById(session.userId).subscribe({
      next: user => {
        this.user = user;
        this.avatarUrl = user.profileImageName ? this.fileUploadService.resolveImageUrl(user.profileImageName) : '';
        this.isOnline = this.liveUpdateService.isUserOnline(user.username);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        Swal.fire('Error', 'Failed to load your profile.', 'error');
      }
    });
  }

  getUserInitials(): string {
    const base = (this.user?.fullName || this.user?.username || 'SU').trim();
    const parts = base.split(/\s+/).filter(Boolean);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return base.substring(0, 2).toUpperCase();
  }

  onAvatarError(): void {
    this.avatarUrl = '';
  }

  openChangePassword(): void {
    this.router.navigate(['/account/change-password']);
  }

  ngOnDestroy(): void {
    this.presenceSubscription?.unsubscribe();
  }
}
