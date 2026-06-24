import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { LookupTypeResponse, LookupValueRequest } from '../../models/lookup.model';
import { LookupService } from '../../services/lookup.service';

@Component({
  selector: 'app-lookup-value-form',
  templateUrl: './lookup-value-form.component.html',
  styleUrls: ['./lookup-value-form.component.scss']
})
export class LookupValueFormComponent implements OnInit {

  loading = false;
  saving = false;
  editId: number | null = null;
  types: LookupTypeResponse[] = [];
  form: LookupValueRequest = {
    lookupTypeId: null,
    valueCode: '',
    valueLabel: '',
    valueBnLabel: '',
    sortOrder: null,
    extraData: '',
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
      this.editId = id || null;
      this.load();
    });
  }

  load(): void {
    this.loading = true;
    forkJoin({
      types: this.lookupService.listTypes(),
      value: this.editId ? this.lookupService.getValueById(this.editId) : of(null)
    }).subscribe({
      next: data => {
        this.types = (data.types || []).filter((item: LookupTypeResponse) => item.status !== 'ARCHIVED');
        if (data.value) {
          this.form = {
            lookupTypeId: data.value.lookupTypeId,
            valueCode: data.value.valueCode,
            valueLabel: data.value.valueLabel,
            valueBnLabel: data.value.valueBnLabel || '',
            sortOrder: data.value.sortOrder ?? null,
            extraData: data.value.extraData || '',
            status: data.value.status
          };
        }
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load lookup value form data.', 'error');
      }
    });
  }

  submit(): void {
    this.saving = true;
    const request = this.editId
      ? this.lookupService.updateValue(this.editId, this.form)
      : this.lookupService.createValue(this.form);
    request.subscribe({
      next: data => {
        this.saving = false;
        Swal.fire('Success', `Lookup value ${this.editId ? 'updated' : 'created'} successfully.`, 'success');
        this.router.navigate(['/lookups/values', data.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', `Failed to ${this.editId ? 'update' : 'create'} lookup value.`, 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/lookups/values']);
  }
}
