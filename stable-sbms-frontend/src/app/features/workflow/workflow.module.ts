import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { WorkflowRoutingModule } from './workflow-routing.module';

import { WorkflowDashboardComponent } from './pages/workflow-dashboard/workflow-dashboard.component';
import { WorkflowHistoryListComponent } from './pages/workflow-history-list/workflow-history-list.component';
import { WorkflowHistoryViewComponent } from './pages/workflow-history-view/workflow-history-view.component';
import { PendingApprovalQueueComponent } from './pages/pending-approval-queue/pending-approval-queue.component';
import { MySubmissionsComponent } from './pages/my-submissions/my-submissions.component';

@NgModule({
  declarations: [
    WorkflowDashboardComponent,
    WorkflowHistoryListComponent,
    WorkflowHistoryViewComponent,
    PendingApprovalQueueComponent,
    MySubmissionsComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    WorkflowRoutingModule
  ]
})
export class WorkflowModule {}
