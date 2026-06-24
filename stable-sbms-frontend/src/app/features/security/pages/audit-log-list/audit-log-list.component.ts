import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { AuditLogResponse } from '../../models/security.model';
import { SecurityService } from '../../services/security.service';

@Component({
  selector: 'app-audit-log-list',
  templateUrl: './audit-log-list.component.html',
  styleUrls: ['./audit-log-list.component.scss']
})
export class AuditLogListComponent implements OnInit {

  loading = false;
  allItems: AuditLogResponse[] = [];
  items: AuditLogResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  userImageMap: Record<string, string> = {};
  filters = {
    keyword: '',
    moduleName: ''
  };

  constructor(
    private securityService: SecurityService,
    private userApi: UserApiService,
    private fileUploadService: FileUploadService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.load();
  }

  load(): void {
    this.loading = true;
    this.securityService.getAuditLogs().subscribe({
      next: data => {
        this.allItems = data || [];
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load audit logs.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.keyword.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.moduleName.toLowerCase().includes(keyword)
        || item.actionName.toLowerCase().includes(keyword)
        || item.performedBy.toLowerCase().includes(keyword);
      const matchesModule = !this.filters.moduleName || item.moduleName === this.filters.moduleName;
      return matchesKeyword && matchesModule;
    });
    this.total = filtered.length;
    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  onSearch(): void {
    this.page = 1;
    this.applyFilters();
  }

  onReset(): void {
    this.filters = { keyword: '', moduleName: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openView(item: AuditLogResponse): void {
    this.router.navigate(['/security/audit-logs', item.id]);
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
