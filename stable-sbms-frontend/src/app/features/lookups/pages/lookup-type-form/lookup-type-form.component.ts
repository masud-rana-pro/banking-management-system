import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { LookupTypeRequest } from '../../models/lookup.model';
import { LookupService } from '../../services/lookup.service';

@Component({
  selector: 'app-lookup-type-form',
  templateUrl: './lookup-type-form.component.html',
  styleUrls: ['./lookup-type-form.component.scss']
})
export class LookupTypeFormComponent implements OnInit {

  loading = false;
  saving = false;
  editId: number | null = null;
  form: LookupTypeRequest = {
    typeCode: '',
    typeName: '',
    description: '',
    status: 'ACTIVE'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private lookupService: LookupService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) {
        this.editId = id;
        this.load(id);
      }
    });
  }

  load(id: number): void {
    this.loading = true;
    this.lookupService.getTypeById(id).subscribe({
      next: data => {
        this.form = {
          typeCode: data.typeCode,
          typeName: data.typeName,
          description: data.description || '',
          status: data.status
        };
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load lookup type detail.', 'error');
      }
    });
  }

  submit(): void {
    this.saving = true;
    const request = this.editId
      ? this.lookupService.updateType(this.editId, this.form)
      : this.lookupService.createType(this.form);
    request.subscribe({
      next: data => {
        this.saving = false;
        Swal.fire('Success', `Lookup type ${this.editId ? 'updated' : 'created'} successfully.`, 'success');
        this.router.navigate(['/lookups/types', data.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', `Failed to ${this.editId ? 'update' : 'create'} lookup type.`, 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/lookups/types']);
  }
}
