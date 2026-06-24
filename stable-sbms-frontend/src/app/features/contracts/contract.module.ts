import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { ContractRoutingModule } from './contract-routing.module';

import { ContractDashboardComponent } from './pages/contract-dashboard/contract-dashboard.component';
import { ContractTemplateListComponent } from './pages/contract-template-list/contract-template-list.component';
import { ContractTemplateCreateComponent } from './pages/contract-template-create/contract-template-create.component';
import { ContractTemplateEditComponent } from './pages/contract-template-edit/contract-template-edit.component';
import { ContractTemplateViewComponent } from './pages/contract-template-view/contract-template-view.component';
import { ContractListComponent } from './pages/contract-list/contract-list.component';
import { ContractGenerateComponent } from './pages/contract-generate/contract-generate.component';
import { ContractViewComponent } from './pages/contract-view/contract-view.component';
import { ContractSignComponent } from './pages/contract-sign/contract-sign.component';
import { ContractVersionHistoryComponent } from './pages/contract-version-history/contract-version-history.component';

@NgModule({
  declarations: [
    ContractDashboardComponent,
    ContractTemplateListComponent,
    ContractTemplateCreateComponent,
    ContractTemplateEditComponent,
    ContractTemplateViewComponent,
    ContractListComponent,
    ContractGenerateComponent,
    ContractViewComponent,
    ContractSignComponent,
    ContractVersionHistoryComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ContractRoutingModule
  ]
})
export class ContractModule {}
