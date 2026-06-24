import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { LookupValueResponse, formatEnumLabel } from '../../models/lookup.model';
import { LookupService } from '../../services/lookup.service';

@Component({
  selector: 'app-lookup-value-view',
  templateUrl: './lookup-value-view.component.html',
  styleUrls: ['./lookup-value-view.component.scss']
})
export class LookupValueViewComponent implements OnInit {

  loading = false;
  item: LookupValueResponse | null = null;

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
    this.lookupService.getValueById(id).subscribe({
      next: data => {
        this.item = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load lookup value detail.', 'error');
      }
    });
  }

  back(): void { this.router.navigate(['/lookups/values']); }
  edit(): void { if (this.item) this.router.navigate(['/lookups/values', this.item.id, 'edit']); }
  label(value?: string | null): string { return formatEnumLabel(value); }
}
