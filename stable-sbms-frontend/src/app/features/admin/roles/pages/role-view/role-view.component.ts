import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { RoleResponse } from '../../model/role.model';
import { RoleApiService } from '../../service/role-api.service';

@Component({
  selector: 'app-role-view',
  templateUrl: './role-view.component.html',
  styleUrls: ['./role-view.component.scss']
})
export class RoleViewComponent implements OnInit {

  role?: RoleResponse;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private api: RoleApiService,
    public access: AccessControlService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) return;
    this.loading = true;
    this.api.getById(id).subscribe({
      next: data => {
        this.role = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load role detail.', 'error');
      }
    });
  }

  onBack(): void { this.router.navigate(['/admin/roles']); }
  onEdit(): void { if (this.role) this.router.navigate(['/admin/roles', this.role.id, 'edit']); }
  onMap(): void { if (this.role) this.router.navigate(['/admin/roles', this.role.id, 'permissions']); }
}
