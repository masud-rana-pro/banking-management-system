import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { SecurityRoutingModule } from './security-routing.module';

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

@NgModule({
  declarations: [
    SecurityDashboardComponent,
    SecurityEventListComponent,
    SecurityEventViewComponent,
    SuspiciousActivityListComponent,
    SuspiciousActivityViewComponent,
    AuditLogListComponent,
    AuditLogViewComponent,
    InvestigationCaseListComponent,
    InvestigationCaseViewComponent,
    InvestigationCaseActionComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    SecurityRoutingModule
  ]
})
export class SecurityModule {}
