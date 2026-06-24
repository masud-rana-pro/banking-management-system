import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { StatementRoutingModule } from './statement-routing.module';

import { StatementDashboardComponent } from './statement-dashboard/statement-dashboard.component';
import { CustomerStatementRequestComponent } from './customer-statement-request/customer-statement-request.component';
import { CustomerStatementListComponent } from './customer-statement-list/customer-statement-list.component';
import { CustomerStatementViewComponent } from './customer-statement-view/customer-statement-view.component';
import { BranchStatementRequestComponent } from './branch-statement-request/branch-statement-request.component';
import { BranchStatementListComponent } from './branch-statement-list/branch-statement-list.component';
import { BranchStatementViewComponent } from './branch-statement-view/branch-statement-view.component';
import { ExportCenterComponent } from './export-center/export-center.component';

@NgModule({
  declarations: [
    StatementDashboardComponent,
    CustomerStatementRequestComponent,
    CustomerStatementListComponent,
    CustomerStatementViewComponent,
    BranchStatementRequestComponent,
    BranchStatementListComponent,
    BranchStatementViewComponent,
    ExportCenterComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    StatementRoutingModule
  ]
})
export class StatementModule {}
