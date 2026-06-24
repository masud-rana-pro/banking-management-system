import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { WorkflowDashboardComponent } from './pages/workflow-dashboard/workflow-dashboard.component';
import { WorkflowHistoryListComponent } from './pages/workflow-history-list/workflow-history-list.component';
import { WorkflowHistoryViewComponent } from './pages/workflow-history-view/workflow-history-view.component';
import { PendingApprovalQueueComponent } from './pages/pending-approval-queue/pending-approval-queue.component';
import { MySubmissionsComponent } from './pages/my-submissions/my-submissions.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: WorkflowDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'WORKFLOW_SUPPORT_ACCESS' } },
  { path: 'history', component: WorkflowHistoryListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'WORKFLOW_SUPPORT_ACCESS' } },
  { path: 'history/:id', component: WorkflowHistoryViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'WORKFLOW_SUPPORT_ACCESS' } },
  { path: 'pending', component: PendingApprovalQueueComponent, canActivate: [PermissionGuard], data: { permissionCode: 'WORKFLOW_SUPPORT_ACCESS' } },
  { path: 'my-submissions', component: MySubmissionsComponent, canActivate: [PermissionGuard], data: { permissionCode: 'WORKFLOW_SUPPORT_ACCESS' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class WorkflowRoutingModule {}
