import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { KycDashboardComponent } from './pages/kyc-dashboard/kyc-dashboard.component';
import { KycListComponent } from './pages/kyc-list/kyc-list.component';
import { KycEditComponent } from './pages/kyc-edit/kyc-edit.component';
import { KycViewComponent } from './pages/kyc-view/kyc-view.component';
import { KycReviewComponent } from './pages/kyc-review/kyc-review.component';
import { KycApprovalQueueComponent } from './pages/kyc-approval-queue/kyc-approval-queue.component';
import { KycDocumentUploadComponent } from './pages/kyc-document-upload/kyc-document-upload.component';
import { KycHistoryComponent } from './pages/kyc-history/kyc-history.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: KycDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'KYC_MANAGEMENT_ACCESS' } },
  { path: 'list', component: KycListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'KYC_MANAGEMENT_ACCESS' } },
  { path: 'new', component: KycEditComponent, canActivate: [PermissionGuard], data: { permissionCode: 'KYC_CREATE' } },
  { path: 'approval-queue', component: KycApprovalQueueComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['KYC_VERIFY', 'KYC_APPROVE', 'KYC_REJECT', 'KYC_RETURN'] } },
  { path: ':id/edit', component: KycEditComponent, canActivate: [PermissionGuard], data: { permissionCode: 'KYC_EDIT' } },
  { path: ':id/review', component: KycReviewComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['KYC_VERIFY', 'KYC_APPROVE', 'KYC_REJECT', 'KYC_RETURN'] } },
  { path: ':id/documents', component: KycDocumentUploadComponent, canActivate: [PermissionGuard], data: { permissionCode: 'KYC_DOCUMENT_UPLOAD' } },
  { path: ':id/history', component: KycHistoryComponent, canActivate: [PermissionGuard], data: { permissionCode: 'KYC_MANAGEMENT_ACCESS' } },
  { path: ':id', component: KycViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'KYC_MANAGEMENT_ACCESS' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class KycRoutingModule {}
