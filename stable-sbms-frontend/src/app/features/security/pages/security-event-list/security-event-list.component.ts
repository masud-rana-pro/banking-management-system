import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { SecurityEventResponse, formatEnumLabel } from '../../models/security.model';
import { SecurityService } from '../../services/security.service';

@Component({
  selector: 'app-security-event-list',
  templateUrl: './security-event-list.component.html',
  styleUrls: ['./security-event-list.component.scss']
})
export class SecurityEventListComponent implements OnInit {

  loading = false;
  allItems: SecurityEventResponse[] = [];
  items: SecurityEventResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  userImageMap: Record<string, string> = {};
  filters = {
    keyword: '',
    severityLevel: ''
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
    this.securityService.getSecurityEvents().subscribe({
      next: data => {
        this.allItems = data || [];
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load security events.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.keyword.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.eventCode.toLowerCase().includes(keyword)
        || item.eventName.toLowerCase().includes(keyword)
        || (item.username || '').toLowerCase().includes(keyword)
        || (item.fullName || '').toLowerCase().includes(keyword)
        || (item.referenceModule || '').toLowerCase().includes(keyword);
      const matchesSeverity = !this.filters.severityLevel || item.severityLevel === this.filters.severityLevel;
      return matchesKeyword && matchesSeverity;
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
    this.filters = { keyword: '', severityLevel: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openView(item: SecurityEventResponse): void {
    this.router.navigate(['/security/events', item.id]);
  }

  openSuspicious(): void {
    this.router.navigate(['/security/suspicious-activities']);
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
