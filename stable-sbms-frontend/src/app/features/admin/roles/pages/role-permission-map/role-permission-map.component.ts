import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { RolePermissionResponse, RoleResponse } from '../../model/role.model';
import { RoleApiService } from '../../service/role-api.service';

@Component({
  selector: 'app-role-permission-map',
  templateUrl: './role-permission-map.component.html',
  styleUrls: ['./role-permission-map.component.scss']
})
export class RolePermissionMapComponent implements OnInit {

  private readonly highRiskPermissionCodes = new Set([
    'ROLE_MAP_PERMISSIONS',
    'USER_ASSIGN_ROLE',
    'USER_RESET_PASSWORD',
    'USER_LOCK',
    'USER_UNLOCK',
    'TRANSACTION_REVERSE',
    'FINANCING_VERIFY',
    'FINANCING_REVIEW',
    'FINANCING_APPROVE',
    'FINANCING_REJECT',
    'FINANCING_RETURN',
    'FINANCING_DISBURSE',
    'FINANCING_COLLECT_PAYMENT',
    'CONTRACT_GENERATE',
    'CONTRACT_CUSTOMER_SIGN',
    'CONTRACT_SHARIAH_SIGN',
    'SHARIAH_CHECKLIST_SAVE',
    'SHARIAH_APPROVE',
    'SHARIAH_REJECT',
    'SHARIAH_RETURN',
    'ZAKAT_CALCULATE',
    'CHARITY_PAYOUT_CREATE'
  ]);

  role: RoleResponse | null = null;
  permissions: RolePermissionResponse[] = [];
  grouped: PermissionGroupViewModel[] = [];
  loading = false;
  saving = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private roleApi: RoleApiService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) this.load(id);
    });
  }

  load(id: number): void {
    this.loading = true;
    this.roleApi.getById(id).subscribe({
      next: role => {
        this.role = role;
        this.roleApi.getPermissions(id).subscribe({
          next: permissions => {
            this.permissions = permissions || [];
            this.groupPermissions();
            this.loading = false;
          },
          error: err => {
            console.error(err);
            this.loading = false;
            Swal.fire('Error', 'Failed to load role permissions.', 'error');
          }
        });
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load role detail.', 'error');
      }
    });
  }

  groupPermissions(): void {
    const map = new Map<string, RolePermissionResponse[]>();
    for (const item of this.permissions) {
      if (!map.has(item.moduleName)) map.set(item.moduleName, []);
      map.get(item.moduleName)?.push(item);
    }
    this.grouped = Array.from(map.entries())
      .map(([moduleName, items]) => this.buildGroup(moduleName, items))
      .sort((a, b) => a.moduleLabel.localeCompare(b.moduleLabel));
  }

  toggleGroup(group: PermissionGroupViewModel, allowed: boolean): void {
    for (const item of group.items) {
      item.allowed = allowed;
    }
  }

  getAllowedCount(group: PermissionGroupViewModel): number {
    return group.items.filter(item => item.allowed).length;
  }

  getEnabledCount(): number {
    return this.permissions.filter(item => item.allowed).length;
  }

  getHighRiskEnabledCount(): number {
    return this.permissions.filter(item => item.allowed && this.highRiskPermissionCodes.has(item.permissionCode)).length;
  }

  trackByPermission(_index: number, permission: RolePermissionResponse): string {
    return permission.permissionCode;
  }

  private buildGroup(moduleName: string, items: RolePermissionResponse[]): PermissionGroupViewModel {
    const sorted = [...items].sort((left, right) => {
      if (left.actionName === 'ACCESS') return -1;
      if (right.actionName === 'ACCESS') return 1;
      return left.displayName.localeCompare(right.displayName);
    });
    const highRiskItems = sorted.filter(item => this.highRiskPermissionCodes.has(item.permissionCode));
    const standardItems = sorted.filter(item => !this.highRiskPermissionCodes.has(item.permissionCode));
    return {
      moduleName,
      moduleLabel: this.toReadableLabel(moduleName),
      items: sorted,
      standardItems,
      highRiskItems,
      hasHighRisk: highRiskItems.length > 0
    };
  }

  private toReadableLabel(value: string): string {
    return value
      .toLowerCase()
      .split('_')
      .filter(Boolean)
      .map(part => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }

  save(): void {
    if (!this.role) return;
    this.saving = true;
    this.roleApi.mapPermissions(this.role.id, {
      permissionCodes: this.permissions.filter(item => item.allowed).map(item => item.permissionCode),
      createdBy: 'SYSTEM'
    }).subscribe({
      next: permissions => {
        this.permissions = permissions;
        this.groupPermissions();
        this.saving = false;
        Swal.fire('Success', 'Role permissions mapped successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', 'Failed to save role permissions.', 'error');
      }
    });
  }

  back(): void {
    if (this.role) {
      this.router.navigate(['/admin/roles', this.role.id]);
      return;
    }
    this.router.navigate(['/admin/roles']);
  }
}

interface PermissionGroupViewModel {
  moduleName: string;
  moduleLabel: string;
  items: RolePermissionResponse[];
  standardItems: RolePermissionResponse[];
  highRiskItems: RolePermissionResponse[];
  hasHighRisk: boolean;
}
