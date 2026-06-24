import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { LookupTypeResponse, LookupValueResponse } from '../../models/lookup.model';
import { LookupService } from '../../services/lookup.service';

@Component({
  selector: 'app-lookup-value-list',
  templateUrl: './lookup-value-list.component.html',
  styleUrls: ['./lookup-value-list.component.scss']
})
export class LookupValueListComponent implements OnInit {

  loading = false;
  types: LookupTypeResponse[] = [];
  items: LookupValueResponse[] = [];
  filters = {
    lookupTypeId: '',
    keyword: ''
  };

  constructor(
    private lookupService: LookupService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    const lookupTypeId = this.filters.lookupTypeId ? Number(this.filters.lookupTypeId) : null;
    forkJoin({
      types: this.lookupService.listTypes(),
      values: this.lookupService.listValues({ lookupTypeId, keyword: this.filters.keyword })
    }).subscribe({
      next: data => {
        this.types = data.types || [];
        this.items = data.values || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load lookup values.', 'error');
      }
    });
  }

  onSearch(): void { this.load(); }
  onReset(): void {
    this.filters = { lookupTypeId: '', keyword: '' };
    this.load();
  }

  openNew(): void { this.router.navigate(['/lookups/values/new']); }
  openView(item: LookupValueResponse): void { this.router.navigate(['/lookups/values', item.id]); }
  openEdit(item: LookupValueResponse): void { this.router.navigate(['/lookups/values', item.id, 'edit']); }

  toggleArchive(item: LookupValueResponse): void {
    const request = item.status === 'ARCHIVED'
      ? this.lookupService.restoreValue(item.id)
      : this.lookupService.archiveValue(item.id);
    const actionLabel = item.status === 'ARCHIVED' ? 'restore' : 'archive';
    request.subscribe({
      next: () => {
        Swal.fire('Success', `Lookup value ${actionLabel}d successfully.`, 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', `Failed to ${actionLabel} lookup value.`, 'error');
      }
    });
  }

}
