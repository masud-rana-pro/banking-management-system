import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { RoleResponse } from '../../model/role.model';
import { RoleApiService } from '../../service/role-api.service';

@Component({
  selector: 'app-role-list',
  templateUrl: './role-list.component.html',
  styleUrls: ['./role-list.component.scss']
})
export class RoleListComponent implements OnInit {

  all: RoleResponse[] = [];
  rows: RoleResponse[] = [];
  loading = false;
  page = 1;
  pageSize = 10;
  total = 0;
  filters = {
    search: '',
    status: ''
  };

  constructor(
    private api: RoleApiService,
    private router: Router,
    public access: AccessControlService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.api.getAll().subscribe({
      next: data => {
        this.all = data || [];
        this.applyFilters(true);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load roles.', 'error');
      }
    });
  }

  onSearch(): void { this.applyFilters(true); }
  onReset(): void {
    this.filters = { search: '', status: '' };
    this.applyFilters(true);
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters(false);
  }

  private applyFilters(resetPage: boolean): void {
    if (resetPage) this.page = 1;
    const keyword = this.filters.search.trim().toLowerCase();
    const status = this.filters.status;
    const filtered = this.all.filter(item => {
      const matchesKeyword = !keyword
        || item.code.toLowerCase().includes(keyword)
        || item.name.toLowerCase().includes(keyword)
        || (item.description || '').toLowerCase().includes(keyword);
      const matchesStatus = !status || item.status === status;
      return matchesKeyword && matchesStatus;
    });
    this.total = filtered.length;
    const start = (this.page - 1) * this.pageSize;
    this.rows = filtered.slice(start, start + this.pageSize);
  }

  onView(role: RoleResponse): void { this.router.navigate(['/admin/roles', role.id]); }
  onEdit(role: RoleResponse): void { this.router.navigate(['/admin/roles', role.id, 'edit']); }
  onMapPermissions(role: RoleResponse): void { this.router.navigate(['/admin/roles', role.id, 'permissions']); }

  onToggleStatus(role: RoleResponse): void {
    const canArchive = role.status !== 'INACTIVE' && this.access.hasPermission('ROLE_ARCHIVE');
    const canRestore = role.status === 'INACTIVE' && this.access.hasPermission('ROLE_RESTORE');
    if (!canArchive && !canRestore) {
      Swal.fire('Access Denied', 'You do not have permission for this action.', 'warning');
      return;
    }
    const request = role.status === 'INACTIVE' ? this.api.restore(role.id) : this.api.deactivate(role.id);
    const actionLabel = role.status === 'INACTIVE' ? 'restore' : 'archive';
    request.subscribe({
      next: () => {
        Swal.fire('Success', `Role ${actionLabel}d successfully.`, 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || `Failed to ${actionLabel} role.`, 'error');
      }
    });
  }
}
