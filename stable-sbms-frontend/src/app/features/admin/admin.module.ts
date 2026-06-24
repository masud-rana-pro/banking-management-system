import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AdminRoutingModule } from './admin-routing.module';
import { SharedModule } from 'src/app/shared/shared.module';




import { RoleDashboardComponent } from './roles/pages/role-dashboard/role-dashboard.component';
import { RoleViewComponent } from './roles/pages/role-view/role-view.component';
import { RoleFormComponent } from './roles/pages/role-form/role-form.component';
import { RoleListComponent } from './roles/pages/role-list/role-list.component';
import { RolePermissionMapComponent } from './roles/pages/role-permission-map/role-permission-map.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { UserDashboardComponent } from './users/user-dashboard/user-dashboard.component';
import { UserListComponent } from './users/user-list/user-list.component';
import { UserFormComponent } from './users/user-form/user-form.component';
import { UserViewComponent } from './users/user-view/user-view.component';
import { UserRoleAssignComponent } from './users/user-role-assign/user-role-assign.component';
import { UserPasswordResetComponent } from './users/user-password-reset/user-password-reset.component';
import { UserLockUnlockComponent } from './users/user-lock-unlock/user-lock-unlock.component';



@NgModule({
  declarations: [
    DashboardComponent,

    RoleDashboardComponent,
    RoleListComponent,
    RoleViewComponent,
    RoleFormComponent,
    RolePermissionMapComponent,

    UserDashboardComponent,
    UserListComponent,
    UserFormComponent,
    UserViewComponent,
    UserRoleAssignComponent,
    UserPasswordResetComponent,
    UserLockUnlockComponent,

  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    SharedModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule
  ]
})
export class AdminModule { }
