import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { NotificationDashboardComponent } from './pages/notification-dashboard/notification-dashboard.component';
import { TemplateListComponent } from './pages/template-list/template-list.component';
import { TemplateCreateComponent } from './pages/template-create/template-create.component';
import { TemplateViewComponent } from './pages/template-view/template-view.component';
import { EventRuleListComponent } from './pages/event-rule-list/event-rule-list.component';
import { EventRuleCreateComponent } from './pages/event-rule-create/event-rule-create.component';
import { DeliveryLogListComponent } from './pages/delivery-log-list/delivery-log-list.component';
import { DeliveryLogViewComponent } from './pages/delivery-log-view/delivery-log-view.component';
import { RetryQueueComponent } from './pages/retry-queue/retry-queue.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: NotificationDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'NOTIFICATION_ALERTS_ACCESS' } },
  { path: 'templates', component: TemplateListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'NOTIFICATION_ALERTS_ACCESS' } },
  { path: 'templates/new', component: TemplateCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'NOTIFICATION_TEMPLATE_CREATE' } },
  { path: 'templates/:id/edit', component: TemplateCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'NOTIFICATION_TEMPLATE_EDIT' } },
  { path: 'templates/:id', component: TemplateViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'NOTIFICATION_ALERTS_ACCESS' } },
  { path: 'event-rules', component: EventRuleListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'NOTIFICATION_ALERTS_ACCESS' } },
  { path: 'event-rules/new', component: EventRuleCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'NOTIFICATION_EVENT_RULE_CREATE' } },
  { path: 'logs', component: DeliveryLogListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'NOTIFICATION_ALERTS_ACCESS' } },
  { path: 'logs/:id', component: DeliveryLogViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'NOTIFICATION_ALERTS_ACCESS' } },
  { path: 'retry-queue', component: RetryQueueComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['NOTIFICATION_ALERTS_ACCESS', 'NOTIFICATION_RETRY'] } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NotificationRoutingModule {}
