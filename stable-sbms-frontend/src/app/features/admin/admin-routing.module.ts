import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';
import { UserFormComponent } from './users/user-form/user-form.component';
import { UserListComponent } from './users/user-list/user-list.component';
import { UserDashboardComponent } from './users/user-dashboard/user-dashboard.component';
import { UserViewComponent } from './users/user-view/user-view.component';
import { UserRoleAssignComponent } from './users/user-role-assign/user-role-assign.component';
import { UserPasswordResetComponent } from './users/user-password-reset/user-password-reset.component';
import { UserLockUnlockComponent } from './users/user-lock-unlock/user-lock-unlock.component';
import { RoleDashboardComponent } from './roles/pages/role-dashboard/role-dashboard.component';
import { RoleListComponent } from './roles/pages/role-list/role-list.component';
import { RolePermissionMapComponent } from './roles/pages/role-permission-map/role-permission-map.component';
import { RoleViewComponent } from './roles/pages/role-view/role-view.component';
import { RoleFormComponent } from './roles/pages/role-form/role-form.component';
import { DashboardComponent } from './dashboard/dashboard.component';

const routes: Routes = [
  { path: 'dashboard', component: DashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ADMIN_DASHBOARD_ACCESS' } },
  { path: 'roles/dashboard', component: RoleDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ROLE_MANAGEMENT_ACCESS' } },
  { path: 'roles', component: RoleListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ROLE_VIEW' } },
  { path: 'roles/new', component: RoleFormComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ROLE_CREATE' } },
  { path: 'roles/:id', component: RoleViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ROLE_VIEW' } },
  { path: 'roles/:id/view', component: RoleViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ROLE_VIEW' } },
  { path: 'roles/:id/edit', component: RoleFormComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ROLE_EDIT' } },
  { path: 'roles/:id/permissions', component: RolePermissionMapComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ROLE_MAP_PERMISSIONS' } },

  { path: 'users/dashboard', component: UserDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'USER_MANAGEMENT_ACCESS' } },
  { path: 'users', component: UserListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'USER_VIEW' } },
  { path: 'users/new', component: UserFormComponent, canActivate: [PermissionGuard], data: { permissionCode: 'USER_CREATE' } },
  { path: 'users/:id', component: UserViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'USER_VIEW' } },
  { path: 'users/:id/edit', component: UserFormComponent, canActivate: [PermissionGuard], data: { permissionCode: 'USER_EDIT' } },
  { path: 'users/:id/assign-role', component: UserRoleAssignComponent, canActivate: [PermissionGuard], data: { permissionCode: 'USER_ASSIGN_ROLE' } },
  { path: 'users/:id/reset-password', component: UserPasswordResetComponent, canActivate: [PermissionGuard], data: { permissionCode: 'USER_RESET_PASSWORD' } },
  { path: 'users/:id/lock-unlock', component: UserLockUnlockComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['USER_LOCK', 'USER_UNLOCK'] } },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }







