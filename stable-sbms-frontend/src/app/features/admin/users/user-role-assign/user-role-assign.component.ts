import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { RoleResponse } from '../../roles/model/role.model';
import { RoleApiService } from '../../roles/service/role-api.service';
import { UserResponse } from '../model/user.model';
import { UserApiService } from '../service/user-api.service';

@Component({
  selector: 'app-user-role-assign',
  templateUrl: './user-role-assign.component.html',
  styleUrls: ['./user-role-assign.component.scss']
})
export class UserRoleAssignComponent implements OnInit {
  user?: UserResponse;
  roles: RoleResponse[] = [];
  loading = false;
  saving = false;

  form = this.fb.group({
    roleId: [null as number | null, Validators.required]
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private api: UserApiService,
    private roleApi: RoleApiService,
    private access: AccessControlService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) return;
    this.loading = true;
    this.roleApi.getDropdown().subscribe({ next: roles => this.roles = roles || [] });
    this.api.getById(id).subscribe({
      next: user => {
        this.user = user;
        this.form.patchValue({ roleId: user.roleId || null });
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load user detail.', 'error');
      }
    });
  }

  save(): void {
    if (!this.user || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    this.api.assignRole(this.user.id, {
      roleId: this.form.value.roleId as number,
      actionBy: this.access.session?.username || 'SYSTEM'
    }).subscribe({
      next: user => {
        this.saving = false;
        Swal.fire('Success', 'User role assigned successfully.', 'success');
        this.router.navigate(['/admin/users', user.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to assign role.', 'error');
      }
    });
  }

  back(): void {
    if (this.user) this.router.navigate(['/admin/users', this.user.id]);
    else this.router.navigate(['/admin/users']);
  }

  getProfileImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }
}
