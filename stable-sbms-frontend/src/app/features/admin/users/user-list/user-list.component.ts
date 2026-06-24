import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { LiveUpdateService } from 'src/app/core/services/live-update.service';
import { RoleApiService } from '../../roles/service/role-api.service';
import { RoleResponse } from '../../roles/model/role.model';
import { BranchOption, UserResponse } from '../model/user.model';
import { UserApiService } from '../service/user-api.service';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {
  readonly viewStorageKey = 'sbms.user-list.view-mode';
  all: UserResponse[] = [];
  rows: UserResponse[] = [];
  loading = false;
  viewMode: 'list' | 'grid' = 'list';
  page = 1;
  pageSize = 10;
  total = 0;
  roleOptions: RoleResponse[] = [];
  branchOptions: BranchOption[] = [];
  filters = {
    search: '',
    roleId: null as number | null,
    branchId: null as number | null,
    status: ''
  };

  constructor(
    private api: UserApiService,
    private roleApi: RoleApiService,
    private router: Router,
    public access: AccessControlService,
    private fileUploadService: FileUploadService,
    public liveUpdateService: LiveUpdateService
  ) {}

  ngOnInit(): void {
    this.viewMode = (localStorage.getItem(this.viewStorageKey) as 'list' | 'grid') || 'list';
    this.loadFilters();
    this.load();
  }

  loadFilters(): void {
    this.roleApi.getDropdown().subscribe({ next: roles => this.roleOptions = roles || [] });
    this.api.getBranchDropdown().subscribe({ next: branches => this.branchOptions = (branches || []).filter(item => item.status === 'ACTIVE') });
  }

  load(): void {
    this.loading = true;
    this.api.getAll({
      search: this.filters.search || undefined,
      status: this.filters.status || undefined,
      roleId: this.filters.roleId,
      branchId: this.filters.branchId
    }).subscribe({
      next: data => {
        this.all = data || [];
        this.applyPaging(true);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load users.', 'error');
      }
    });
  }

  onSearch(): void {
    this.load();
  }

  onReset(): void {
    this.filters = { search: '', roleId: null, branchId: null, status: '' };
    this.load();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyPaging(false);
  }

  setViewMode(mode: 'list' | 'grid'): void {
    this.viewMode = mode;
    localStorage.setItem(this.viewStorageKey, mode);
  }

  private applyPaging(resetPage: boolean): void {
    if (resetPage) this.page = 1;
    this.total = this.all.length;
    const start = (this.page - 1) * this.pageSize;
    this.rows = this.all.slice(start, start + this.pageSize);
  }

  onView(user: UserResponse): void { this.router.navigate(['/admin/users', user.id]); }
  onEdit(user: UserResponse): void { this.router.navigate(['/admin/users', user.id, 'edit']); }

  isOnline(user: UserResponse): boolean {
    return this.liveUpdateService.isUserOnline(user.username);
  }

  getConnectionLabel(user: UserResponse): string {
    return this.isOnline(user) ? 'ACTIVE' : 'OFFLINE';
  }

  getImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }

  async onAssignRole(user: UserResponse): Promise<void> {
    const options = this.roleOptions.reduce((acc, role) => {
      acc[String(role.id)] = `${role.code} - ${role.name}`;
      return acc;
    }, {} as Record<string, string>);

    const result = await Swal.fire({
      title: `Assign Role: ${user.username}`,
      input: 'select',
      inputOptions: options,
      inputValue: user.roleId ? String(user.roleId) : undefined,
      inputPlaceholder: 'Select a role',
      showCancelButton: true,
      confirmButtonText: 'Save Role'
    });

    if (!result.isConfirmed || !result.value) return;

    this.api.assignRole(user.id, {
      roleId: Number(result.value),
      actionBy: this.access.session?.username || 'SYSTEM'
    }).subscribe({
      next: () => {
        Swal.fire('Success', 'User role assigned successfully.', 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to assign role.', 'error');
      }
    });
  }

  async onResetPassword(user: UserResponse): Promise<void> {
    const result = await Swal.fire({
      title: `Reset Password: ${user.username}`,
      html: `
        <input id="swal-user-password" type="password" class="swal2-input" placeholder="New password">
        <input id="swal-user-password-confirm" type="password" class="swal2-input" placeholder="Confirm password">
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Reset Password',
      preConfirm: () => {
        const newPassword = (document.getElementById('swal-user-password') as HTMLInputElement | null)?.value || '';
        const confirmPassword = (document.getElementById('swal-user-password-confirm') as HTMLInputElement | null)?.value || '';
        if (!newPassword || newPassword.length < 8) {
          Swal.showValidationMessage('Password must be at least 8 characters.');
          return null;
        }
        if (newPassword !== confirmPassword) {
          Swal.showValidationMessage('Password confirmation does not match.');
          return null;
        }
        return { newPassword, confirmPassword };
      }
    });

    if (!result.isConfirmed || !result.value) return;

    this.api.resetPassword(user.id, {
      newPassword: result.value.newPassword,
      confirmPassword: result.value.confirmPassword,
      actionBy: this.access.session?.username || 'SYSTEM'
    }).subscribe({
      next: () => {
        Swal.fire('Success', 'User password reset successfully.', 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to reset password.', 'error');
      }
    });
  }

  async onLockUnlock(user: UserResponse): Promise<void> {
    const locking = !user.locked;
    const result = await Swal.fire({
      title: `${locking ? 'Lock' : 'Unlock'} User`,
      input: 'text',
      inputLabel: 'Reason',
      inputPlaceholder: locking ? 'Why is this user being locked?' : 'Why is this user being unlocked?',
      showCancelButton: true,
      confirmButtonText: locking ? 'Lock User' : 'Unlock User'
    });

    if (!result.isConfirmed) return;

    const request = locking
      ? this.api.lock(user.id, { actionBy: this.access.session?.username || 'SYSTEM', reason: result.value || null })
      : this.api.unlock(user.id, { actionBy: this.access.session?.username || 'SYSTEM', reason: result.value || null });

    request.subscribe({
      next: () => {
        Swal.fire('Success', `User ${locking ? 'locked' : 'unlocked'} successfully.`, 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || `Failed to ${locking ? 'lock' : 'unlock'} user.`, 'error');
      }
    });
  }

  onToggleArchive(user: UserResponse): void {
    const request = user.status === 'INACTIVE' ? this.api.restore(user.id) : this.api.deactivate(user.id);
    const label = user.status === 'INACTIVE' ? 'restore' : 'archive';
    request.subscribe({
      next: () => {
        Swal.fire('Success', `User ${label}d successfully.`, 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || `Failed to ${label} user.`, 'error');
      }
    });
  }
}
