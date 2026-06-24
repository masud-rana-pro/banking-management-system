import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { ShariahDashboardComponent } from './pages/shariah-dashboard/shariah-dashboard.component';
import { CaseListComponent } from './pages/case-list/case-list.component';
import { CaseViewComponent } from './pages/case-view/case-view.component';
import { CaseReviewComponent } from './pages/case-review/case-review.component';
import { CorrectionQueueComponent } from './pages/correction-queue/correction-queue.component';
import { FatwaCertificateListComponent } from './pages/fatwa-certificate-list/fatwa-certificate-list.component';
import { AnnualReportViewComponent } from './pages/annual-report-view/annual-report-view.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: ShariahDashboardComponent },
  { path: 'cases', component: CaseListComponent },
  { path: 'cases/:id/review', component: CaseReviewComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['SHARIAH_CHECKLIST_SAVE', 'SHARIAH_APPROVE', 'SHARIAH_REJECT', 'SHARIAH_RETURN'] } },
  { path: 'cases/:id', component: CaseViewComponent },
  { path: 'correction-queue', component: CorrectionQueueComponent },
  { path: 'fatwa-certificates', component: FatwaCertificateListComponent },
  { path: 'annual-report', component: AnnualReportViewComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ShariahRoutingModule {}
