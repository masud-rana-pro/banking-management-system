import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { KycRoutingModule } from './kyc-routing.module';

import { KycDashboardComponent } from './pages/kyc-dashboard/kyc-dashboard.component';
import { KycListComponent } from './pages/kyc-list/kyc-list.component';
import { KycEditComponent } from './pages/kyc-edit/kyc-edit.component';
import { KycViewComponent } from './pages/kyc-view/kyc-view.component';
import { KycReviewComponent } from './pages/kyc-review/kyc-review.component';
import { KycApprovalQueueComponent } from './pages/kyc-approval-queue/kyc-approval-queue.component';
import { KycDocumentUploadComponent } from './pages/kyc-document-upload/kyc-document-upload.component';
import { KycHistoryComponent } from './pages/kyc-history/kyc-history.component';

@NgModule({
  declarations: [
    KycDashboardComponent,
    KycListComponent,
    KycEditComponent,
    KycViewComponent,
    KycReviewComponent,
    KycApprovalQueueComponent,
    KycDocumentUploadComponent,
    KycHistoryComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    KycRoutingModule
  ]
})
export class KycModule {}
