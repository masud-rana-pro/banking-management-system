import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from '../model/user.model';
import { UserApiService } from '../service/user-api.service';

@Component({
  selector: 'app-user-lock-unlock',
  templateUrl: './user-lock-unlock.component.html',
  styleUrls: ['./user-lock-unlock.component.scss']
})
export class UserLockUnlockComponent implements OnInit {
  user?: UserResponse;
  loading = false;
  saving = false;

  form = this.fb.group({
    reason: ['']
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private api: UserApiService,
    private access: AccessControlService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) return;
    this.loading = true;
    this.api.getById(id).subscribe({
      next: user => {
        this.user = user;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load user detail.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.user) return;
    this.saving = true;
    const payload = {
      actionBy: this.access.session?.username || 'SYSTEM',
      reason: this.form.value.reason || ''
    };
    const request = this.user.locked
      ? this.api.unlock(this.user.id, payload)
      : this.api.lock(this.user.id, payload);
    request.subscribe({
      next: user => {
        this.saving = false;
        Swal.fire('Success', `User ${this.user?.locked ? 'unlocked' : 'locked'} successfully.`, 'success');
        this.router.navigate(['/admin/users', user.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to update user lock state.', 'error');
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
