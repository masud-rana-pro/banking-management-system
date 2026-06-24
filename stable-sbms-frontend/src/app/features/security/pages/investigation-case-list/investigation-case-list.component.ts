import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { InvestigationCaseResponse, formatEnumLabel } from '../../models/security.model';
import { SecurityService } from '../../services/security.service';

@Component({
  selector: 'app-investigation-case-list',
  templateUrl: './investigation-case-list.component.html',
  styleUrls: ['./investigation-case-list.component.scss']
})
export class InvestigationCaseListComponent implements OnInit {

  loading = false;
  allItems: InvestigationCaseResponse[] = [];
  items: InvestigationCaseResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  userImageMap: Record<string, string> = {};
  filters = {
    keyword: '',
    caseStatus: '',
    caseType: ''
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
    this.securityService.getInvestigationCases().subscribe({
      next: data => {
        this.allItems = data || [];
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load investigation cases.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.keyword.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.caseNo.toLowerCase().includes(keyword)
        || item.referenceModule.toLowerCase().includes(keyword)
        || item.openedBy.toLowerCase().includes(keyword)
        || (item.assignedUsername || '').toLowerCase().includes(keyword)
        || (item.assignedFullName || '').toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.caseStatus || item.caseStatus === this.filters.caseStatus;
      const matchesType = !this.filters.caseType || item.caseType === this.filters.caseType;
      return matchesKeyword && matchesStatus && matchesType;
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
    this.filters = { keyword: '', caseStatus: '', caseType: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openView(item: InvestigationCaseResponse): void {
    this.router.navigate(['/security/investigation-cases', item.id]);
  }

  openAction(item: InvestigationCaseResponse): void {
    this.router.navigate(['/security/investigation-cases', item.id, 'action']);
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
