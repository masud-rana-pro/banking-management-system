import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { SecurityEventResponse, formatEnumLabel } from '../../models/security.model';
import { SecurityService } from '../../services/security.service';

@Component({
  selector: 'app-security-event-view',
  templateUrl: './security-event-view.component.html',
  styleUrls: ['./security-event-view.component.scss']
})
export class SecurityEventViewComponent implements OnInit {

  loading = false;
  item: SecurityEventResponse | null = null;
  userImageMap: Record<string, string> = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private securityService: SecurityService,
    private userApi: UserApiService,
    private fileUploadService: FileUploadService
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
    this.securityService.getSecurityEventById(id).subscribe({
      next: data => {
        this.item = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load security event detail.', 'error');
      }
    });
  }

  openList(): void {
    this.router.navigate(['/security/events']);
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
