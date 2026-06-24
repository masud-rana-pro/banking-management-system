import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { RoleApiService } from '../../service/role-api.service';

@Component({
  selector: 'app-role-form',
  templateUrl: './role-form.component.html',
  styleUrls: ['./role-form.component.scss']
})
export class RoleFormComponent implements OnInit {

  isEdit = false;
  roleId?: number;
  loading = false;
  saving = false;

  form = this.fb.group({
    code: ['', Validators.required],
    name: ['', [Validators.required, Validators.minLength(2)]],
    description: [''],
    status: ['ACTIVE', Validators.required]
  });

  constructor(
    private fb: FormBuilder,
    private api: RoleApiService,
    private route: ActivatedRoute,
    private router: Router,
    private access: AccessControlService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEdit = true;
      this.roleId = Number(idParam);
      this.loadRole(this.roleId);
    }
  }

  loadRole(id: number): void {
    this.loading = true;
    this.api.getById(id).subscribe({
      next: role => {
        this.form.patchValue({
          code: role.code,
          name: role.name,
          description: role.description || '',
          status: role.status
        });
        this.form.get('code')?.disable();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load role detail.', 'error');
      }
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    const payload = {
      code: this.form.getRawValue().code || '',
      name: this.form.getRawValue().name || '',
      description: this.form.getRawValue().description || '',
      status: this.form.getRawValue().status || 'ACTIVE',
      actionBy: this.access.session?.username || 'SYSTEM'
    };
    const request = this.isEdit && this.roleId
      ? this.api.update(this.roleId, payload)
      : this.api.create(payload);
    request.subscribe({
      next: role => {
        this.saving = false;
        Swal.fire('Success', `Role ${this.isEdit ? 'updated' : 'created'} successfully.`, 'success');
        this.router.navigate(['/admin/roles', role.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save role.', 'error');
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/admin/roles']);
  }
}
