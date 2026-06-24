import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { ShariahRoutingModule } from './shariah-routing.module';

import { ShariahDashboardComponent } from './pages/shariah-dashboard/shariah-dashboard.component';
import { CaseListComponent } from './pages/case-list/case-list.component';
import { CaseViewComponent } from './pages/case-view/case-view.component';
import { CaseReviewComponent } from './pages/case-review/case-review.component';
import { CorrectionQueueComponent } from './pages/correction-queue/correction-queue.component';
import { FatwaCertificateListComponent } from './pages/fatwa-certificate-list/fatwa-certificate-list.component';
import { AnnualReportViewComponent } from './pages/annual-report-view/annual-report-view.component';

@NgModule({
  declarations: [
    ShariahDashboardComponent,
    CaseListComponent,
    CaseViewComponent,
    CaseReviewComponent,
    CorrectionQueueComponent,
    FatwaCertificateListComponent,
    AnnualReportViewComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ShariahRoutingModule
  ]
})
export class ShariahModule {}
