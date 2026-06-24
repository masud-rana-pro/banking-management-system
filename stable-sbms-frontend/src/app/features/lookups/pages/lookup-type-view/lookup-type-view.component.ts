import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { LookupTypeResponse, LookupValueResponse } from '../../models/lookup.model';
import { LookupService } from '../../services/lookup.service';

@Component({
  selector: 'app-lookup-type-view',
  templateUrl: './lookup-type-view.component.html',
  styleUrls: ['./lookup-type-view.component.scss']
})
export class LookupTypeViewComponent implements OnInit {

  loading = false;
  item: LookupTypeResponse | null = null;
  values: LookupValueResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private lookupService: LookupService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) this.load(id);
    });
  }

  load(id: number): void {
    this.loading = true;
    forkJoin({
      type: this.lookupService.getTypeById(id),
      values: this.lookupService.listValues({ lookupTypeId: id })
    }).subscribe({
      next: data => {
        this.item = data.type;
        this.values = data.values || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load lookup type detail.', 'error');
      }
    });
  }

  back(): void { this.router.navigate(['/lookups/types']); }
  edit(): void { if (this.item) this.router.navigate(['/lookups/types', this.item.id, 'edit']); }
}
