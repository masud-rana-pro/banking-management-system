import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { LookupTypeResponse } from '../../models/lookup.model';
import { LookupService } from '../../services/lookup.service';

@Component({
  selector: 'app-lookup-type-list',
  templateUrl: './lookup-type-list.component.html',
  styleUrls: ['./lookup-type-list.component.scss']
})
export class LookupTypeListComponent implements OnInit {

  loading = false;
  allItems: LookupTypeResponse[] = [];
  items: LookupTypeResponse[] = [];
  keyword = '';
  page = 1;
  pageSize = 10;
  total = 0;

  constructor(
    private lookupService: LookupService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.lookupService.listTypes().subscribe({
      next: data => {
        this.allItems = data || [];
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load lookup types.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.keyword.trim().toLowerCase();
    const filtered = this.allItems.filter(item => !keyword
      || item.typeCode.toLowerCase().includes(keyword)
      || item.typeName.toLowerCase().includes(keyword)
      || (item.description || '').toLowerCase().includes(keyword));
    this.total = filtered.length;
    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  onSearch(): void {
    this.page = 1;
    this.applyFilters();
  }

  onReset(): void {
    this.keyword = '';
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openNew(): void { this.router.navigate(['/lookups/types/new']); }
  openView(item: LookupTypeResponse): void { this.router.navigate(['/lookups/types', item.id]); }
  openEdit(item: LookupTypeResponse): void { this.router.navigate(['/lookups/types', item.id, 'edit']); }

  toggleArchive(item: LookupTypeResponse): void {
    const request = item.status === 'ARCHIVED'
      ? this.lookupService.restoreType(item.id)
      : this.lookupService.archiveType(item.id);
    const actionLabel = item.status === 'ARCHIVED' ? 'restore' : 'archive';
    request.subscribe({
      next: () => {
        Swal.fire('Success', `Lookup type ${actionLabel}d successfully.`, 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', `Failed to ${actionLabel} lookup type.`, 'error');
      }
    });
  }

}
