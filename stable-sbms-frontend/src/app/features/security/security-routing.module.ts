import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { SecurityDashboardComponent } from './pages/security-dashboard/security-dashboard.component';
import { SecurityEventListComponent } from './pages/security-event-list/security-event-list.component';
import { SecurityEventViewComponent } from './pages/security-event-view/security-event-view.component';
import { SuspiciousActivityListComponent } from './pages/suspicious-activity-list/suspicious-activity-list.component';
import { SuspiciousActivityViewComponent } from './pages/suspicious-activity-view/suspicious-activity-view.component';
import { AuditLogListComponent } from './pages/audit-log-list/audit-log-list.component';
import { AuditLogViewComponent } from './pages/audit-log-view/audit-log-view.component';
import { InvestigationCaseListComponent } from './pages/investigation-case-list/investigation-case-list.component';
import { InvestigationCaseViewComponent } from './pages/investigation-case-view/investigation-case-view.component';
import { InvestigationCaseActionComponent } from './pages/investigation-case-action/investigation-case-action.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: SecurityDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'SECURITY_AUDIT_ACCESS' } },
  { path: 'events', component: SecurityEventListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'SECURITY_AUDIT_ACCESS' } },
  { path: 'events/:id', component: SecurityEventViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'SECURITY_AUDIT_ACCESS' } },
  { path: 'suspicious-activities', component: SuspiciousActivityListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'SECURITY_AUDIT_ACCESS' } },
  { path: 'suspicious-activities/:id', component: SuspiciousActivityViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'SECURITY_AUDIT_ACCESS' } },
  { path: 'audit-logs', component: AuditLogListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'SECURITY_AUDIT_ACCESS' } },
  { path: 'audit-logs/:id', component: AuditLogViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'SECURITY_AUDIT_ACCESS' } },
  { path: 'investigation-cases', component: InvestigationCaseListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'SECURITY_AUDIT_ACCESS' } },
  { path: 'investigation-cases/:id', component: InvestigationCaseViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'SECURITY_AUDIT_ACCESS' } },
  { path: 'investigation-cases/:id/action', component: InvestigationCaseActionComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['SECURITY_INVESTIGATION_ASSIGN', 'SECURITY_INVESTIGATION_CLOSE'] } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SecurityRoutingModule {}
